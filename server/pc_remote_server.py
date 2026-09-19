#!/usr/bin/env python3
"""
=============================================================================
SmartRemote Pro - PC Companion Server
Turns your laptop/desktop into a receiver for SmartRemote Pro Mobile App.
Supports: Wireless Touchpad Mouse, Smooth Scroll, Keyboard, PPT Clicker.
Compatible with: Windows (native ctypes), macOS, and Linux.
=============================================================================
"""

import socket
import sys
import time
import os
import platform

UDP_PORT = 8765
BUFFER_SIZE = 1024

IS_WINDOWS = platform.system() == "Windows"

if IS_WINDOWS:
    import ctypes
    user32 = ctypes.windll.user32
    
    # Windows Mouse Event Constants
    MOUSEEVENTF_MOVE = 0x0001
    MOUSEEVENTF_LEFTDOWN = 0x0002
    MOUSEEVENTF_LEFTUP = 0x0004
    MOUSEEVENTF_RIGHTDOWN = 0x0008
    MOUSEEVENTF_RIGHTUP = 0x0010
    MOUSEEVENTF_MIDDLEDOWN = 0x0020
    MOUSEEVENTF_MIDDLEUP = 0x0040
    MOUSEEVENTF_WHEEL = 0x0800

    # Windows Virtual Key Codes
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
        # Windows WHEEL delta is typically 120 per notch
        user32.mouse_event(MOUSEEVENTF_WHEEL, 0, 0, delta * 120, 0)

    def press_key(vk_code: int):
        user32.keybd_event(vk_code, 0, 0, 0)
        time.sleep(0.02)
        user32.keybd_event(vk_code, 0, 2, 0)  # KEYEVENTF_KEYUP = 2

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
            press_key(VK_NEXT)  # Page Down
        elif action == "prev":
            press_key(VK_PRIOR) # Page Up
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

else:
    # Fallback for macOS / Linux using pyautogui if available
    try:
        import pyautogui
        pyautogui.PAUSE = 0.001
        
        def move_mouse(dx: int, dy: int):
            pyautogui.moveRel(dx, dy)

        def mouse_click(button: str):
            pyautogui.click(button=button)

        def mouse_scroll(delta: int):
            pyautogui.scroll(delta * 5)

        def send_key_shortcut(name: str):
            if name == "win":
                pyautogui.press("command" if sys.platform == "darwin" else "win")
            elif name == "alt_tab":
                pyautogui.hotkey("alt", "tab")
            elif name == "ctrl_c":
                pyautogui.hotkey("ctrl", "c")
            elif name == "ctrl_v":
                pyautogui.hotkey("ctrl", "v")
            elif name == "esc":
                pyautogui.press("esc")
            elif name == "enter":
                pyautogui.press("enter")

        def send_ppt_nav(action: str):
            if action == "next":
                pyautogui.press("pagedown")
            elif action == "prev":
                pyautogui.press("pageup")
            elif action == "f5":
                pyautogui.press("f5")
            elif action == "black":
                pyautogui.press("b")
            elif action == "esc":
                pyautogui.press("esc")

        def type_text(text: str):
            pyautogui.write(text)

    except ImportError:
        print("[WARNING] Non-Windows OS detected and 'pyautogui' not installed.")
        def move_mouse(dx, dy): pass
        def mouse_click(b): pass
        def mouse_scroll(d): pass
        def send_key_shortcut(n): pass
        def send_ppt_nav(a): pass
        def type_text(t): pass


def get_local_ip():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception:
        return "127.0.0.1"


def start_server():
    server_ip = get_local_ip()
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind(("0.0.0.0", UDP_PORT))

    hostname = platform.node()
    print("=" * 60)
    print("  SmartRemote Pro - PC Wireless Mouse & Touchpad Server")
    print("=" * 60)
    print(f"  Status:       RUNNING")
    print(f"  Laptop IP:    {server_ip}")
    print(f"  Port:         {UDP_PORT}")
    print(f"  Hostname:     {hostname}")
    print(f"  OS:           {platform.system()} {platform.release()}")
    print("-" * 60)
    print("  Listening for mobile app commands...")
    print("  Press Ctrl+C to stop the server.\n")

    while True:
        try:
            data, addr = sock.recvfrom(BUFFER_SIZE)
            message = data.decode("utf-8").strip()

            # Handle Auto-Discovery Ping
            if message == "DISCOVER_PC_REMOTE":
                ack_response = f"PC_REMOTE_ACK|{hostname}|{platform.system()}".encode("utf-8")
                sock.sendto(ack_response, addr)
                continue

            parts = message.split(" ", 2)
            cmd = parts[0].upper()

            if cmd == "MOVE" and len(parts) >= 3:
                dx = int(parts[1])
                dy = int(parts[2])
                move_mouse(dx, dy)

            elif cmd == "CLICK" and len(parts) >= 2:
                btn = parts[1].lower()
                mouse_click(btn)

            elif cmd == "SCROLL" and len(parts) >= 2:
                delta = int(parts[1])
                mouse_scroll(delta)

            elif cmd == "KEY" and len(parts) >= 2:
                key_name = parts[1].lower()
                send_key_shortcut(key_name)

            elif cmd == "PPT" and len(parts) >= 2:
                ppt_action = parts[1].lower()
                send_ppt_nav(ppt_action)

            elif cmd == "TEXT" and len(parts) >= 2:
                text_content = parts[1]
                type_text(text_content)

        except KeyboardInterrupt:
            print("\n[INFO] Stopping PC Remote Server...")
            break
        except Exception as e:
            # Silently ignore corrupted packets
            pass

    sock.close()
    print("[INFO] Server stopped.")


if __name__ == "__main__":
    start_server()
