#!/usr/bin/env python3
"""
=============================================================================
SmartRemote Pro - Unified Web & WebSocket Remote Server
Serves the Web Application on HTTP (Port 8080) and handles real-time
low-latency PC mouse, keyboard, media, and remote commands on WebSocket (Port 8765).
=============================================================================
"""

import asyncio
import ctypes
import http.server
import os
import platform
import socket
import sys
import threading
import time
import webbrowser
import websockets

HTTP_PORT = 8080
WS_PORT = 8765
IS_WINDOWS = platform.system() == "Windows"

# Setup Windows native input constants if on Windows
if IS_WINDOWS:
    user32 = ctypes.windll.user32

    MOUSEEVENTF_MOVE = 0x0001
    MOUSEEVENTF_LEFTDOWN = 0x0002
    MOUSEEVENTF_LEFTUP = 0x0004
    MOUSEEVENTF_RIGHTDOWN = 0x0008
    MOUSEEVENTF_RIGHTUP = 0x0010
    MOUSEEVENTF_MIDDLEDOWN = 0x0020
    MOUSEEVENTF_MIDDLEUP = 0x0040
    MOUSEEVENTF_WHEEL = 0x0800

    VK_ESCAPE = 0x1B
    VK_RETURN = 0x0D
    VK_LWIN = 0x5B
    VK_PRIOR = 0x21  # Page Up
    VK_NEXT = 0x22   # Page Down
    VK_F5 = 0x74
    VK_KEY_B = 0x42
    VK_TAB = 0x09
    VK_MENU = 0x12   # Alt
    VK_CONTROL = 0x11
    VK_KEY_C = 0x43
    VK_KEY_V = 0x56

    # Multimedia Keys
    VK_VOLUME_MUTE = 0xAD
    VK_VOLUME_DOWN = 0xAE
    VK_VOLUME_UP = 0xAF
    VK_MEDIA_NEXT_TRACK = 0xB0
    VK_MEDIA_PREV_TRACK = 0xB1
    VK_MEDIA_PLAY_PAUSE = 0xB3

    def move_mouse(dx: int, dy: int):
        user32.mouse_event(MOUSEEVENTF_MOVE, dx, dy, 0, 0)

    def mouse_click(button: str):
        if button == "left":
            user32.mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0)
            user32.mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0)
        elif button == "right":
            user32.mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0)
            user32.mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0)
        elif button == "middle":
            user32.mouse_event(MOUSEEVENTF_MIDDLEDOWN, 0, 0, 0, 0)
            user32.mouse_event(MOUSEEVENTF_MIDDLEUP, 0, 0, 0, 0)

    def mouse_scroll(delta: int):
        user32.mouse_event(MOUSEEVENTF_WHEEL, 0, 0, delta * 120, 0)

    def press_key(vk_code: int):
        user32.keybd_event(vk_code, 0, 0, 0)
        time.sleep(0.02)
        user32.keybd_event(vk_code, 0, 2, 0)

    def send_key_shortcut(name: str):
        if name == "win":
            press_key(VK_LWIN)
        elif name == "alt_tab":
            user32.keybd_event(VK_MENU, 0, 0, 0)
            user32.keybd_event(VK_TAB, 0, 0, 0)
            time.sleep(0.05)
            user32.keybd_event(VK_TAB, 0, 2, 0)
            user32.keybd_event(VK_MENU, 0, 2, 0)
        elif name == "ctrl_c":
            user32.keybd_event(VK_CONTROL, 0, 0, 0)
            user32.keybd_event(VK_KEY_C, 0, 0, 0)
            time.sleep(0.02)
            user32.keybd_event(VK_KEY_C, 0, 2, 0)
            user32.keybd_event(VK_CONTROL, 0, 2, 0)
        elif name == "ctrl_v":
            user32.keybd_event(VK_CONTROL, 0, 0, 0)
            user32.keybd_event(VK_KEY_V, 0, 0, 0)
            time.sleep(0.02)
            user32.keybd_event(VK_KEY_V, 0, 2, 0)
            user32.keybd_event(VK_CONTROL, 0, 2, 0)
        elif name == "esc":
            press_key(VK_ESCAPE)
        elif name == "enter":
            press_key(VK_RETURN)

    def send_ppt_nav(action: str):
        if action == "next":
            press_key(VK_NEXT)
        elif action == "prev":
            press_key(VK_PRIOR)
        elif action == "f5":
            press_key(VK_F5)
        elif action == "black":
            press_key(VK_KEY_B)
        elif action == "esc":
            press_key(VK_ESCAPE)

    def type_text(text: str):
        for char in text:
            vk = user32.VkKeyScanW(ord(char))
            user32.keybd_event(vk & 0xFF, 0, 0, 0)
            user32.keybd_event(vk & 0xFF, 0, 2, 0)
            time.sleep(0.01)

    def adjust_pc_volume(action: str):
        if action == "up":
            press_key(VK_VOLUME_UP)
        elif action == "down":
            press_key(VK_VOLUME_DOWN)
        elif action == "mute":
            press_key(VK_VOLUME_MUTE)

else:
    # Fallback using pyautogui
    try:
        import pyautogui
        def move_mouse(dx: int, dy: int): pyautogui.moveRel(dx, dy)
        def mouse_click(b: str): pyautogui.click(button=b)
        def mouse_scroll(d: int): pyautogui.scroll(d * 5)
        def send_key_shortcut(n: str): pass
        def send_ppt_nav(a: str): pass
        def type_text(t: str): pyautogui.write(t)
        def adjust_pc_volume(a: str): pass
    except ImportError:
        def move_mouse(dx, dy): pass
        def mouse_click(b): pass
        def mouse_scroll(d): pass
        def send_key_shortcut(n): pass
        def send_ppt_nav(a): pass
        def type_text(t): pass
        def adjust_pc_volume(a): pass


def get_local_ip():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception:
        return "127.0.0.1"


# HTTP Static File Server Handler
class WebAppHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        # Point to the web directory
        web_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "web"))
        super().__init__(*args, directory=web_dir, **kwargs)

    def log_message(self, format, *args):
        # Suppress noisy HTTP asset logs
        pass


def start_http_server():
    server = http.server.ThreadingHTTPServer(("0.0.0.0", HTTP_PORT), WebAppHandler)
    server.serve_forever()


# WebSocket Handler for Real-Time Commands
async def ws_handler(websocket):
    hostname = platform.node()
    async for message in websocket:
        try:
            msg = message.strip()

            if msg == "DISCOVER_PC_REMOTE":
                ack = f"PC_REMOTE_ACK|{hostname}|{platform.system()}"
                await websocket.send(ack)
                continue

            parts = msg.split(" ", 2)
            cmd = parts[0].upper()

            if cmd == "MOVE" and len(parts) >= 3:
                move_mouse(int(parts[1]), int(parts[2]))

            elif cmd == "CLICK" and len(parts) >= 2:
                mouse_click(parts[1].lower())

            elif cmd == "SCROLL" and len(parts) >= 2:
                mouse_scroll(int(parts[1]))

            elif cmd == "KEY" and len(parts) >= 2:
                send_key_shortcut(parts[1].lower())

            elif cmd == "PPT" and len(parts) >= 2:
                send_ppt_nav(parts[1].lower())

            elif cmd == "TEXT" and len(parts) >= 2:
                type_text(parts[1])

            elif cmd == "TV_VOL_UP":
                adjust_pc_volume("up")

            elif cmd == "TV_VOL_DOWN":
                adjust_pc_volume("down")

            elif cmd == "TV_MUTE":
                adjust_pc_volume("mute")

            elif cmd == "TV_LAUNCH" and len(parts) >= 2:
                target_app = parts[1].upper()
                if "YOUTUBE" in target_app:
                    webbrowser.open("https://www.youtube.com")
                elif "NETFLIX" in target_app:
                    webbrowser.open("https://www.netflix.com")
                elif "PRIME" in target_app:
                    webbrowser.open("https://www.primevideo.com")
                elif "HOTSTAR" in target_app:
                    webbrowser.open("https://www.hotstar.com")

        except Exception as e:
            pass


async def main():
    if hasattr(sys.stdout, "reconfigure"):
        try:
            sys.stdout.reconfigure(encoding="utf-8")
        except Exception:
            pass

    local_ip = get_local_ip()
    hostname = platform.node()

    # Start HTTP static file server in background daemon thread
    http_thread = threading.Thread(target=start_http_server, daemon=True)
    http_thread.start()

    print("=" * 65)
    print("      SmartRemote Pro - Web App & PC Remote Server")
    print("=" * 65)
    print(f"  * Web App URL (This PC):    http://localhost:{HTTP_PORT}")
    print(f"  * Web App URL (Mobile WiFi): http://{local_ip}:{HTTP_PORT}")
    print(f"  * WebSocket Port:           {WS_PORT}")
    print(f"  * Machine Hostname:         {hostname}")
    print(f"  * Operating System:         {platform.system()} {platform.release()}")
    print("-" * 65)
    print("  [INFO] Open the Mobile URL on your phone to use as a remote & mouse!")
    print("  [INFO] Press Ctrl+C to stop the server.\n")

    # Start WebSocket Server
    async with websockets.serve(ws_handler, "0.0.0.0", WS_PORT):
        await asyncio.Future()  # run forever


if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("\n[INFO] SmartRemote Pro server stopped.")
        sys.exit(0)
