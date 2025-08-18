// stealth.cpp
#include <string_view>
#include <windows.h>
#include <tlhelp32.h>

#define WDA_EXCLUDEFROMCAPTURE 0x11

// Try to find the main visible window of the current process
HWND FindMainWindow() {
    DWORD currentPID = GetCurrentProcessId();
    HWND hwnd = nullptr;

    hwnd = FindWindowEx(nullptr, nullptr, nullptr, nullptr);
    while (hwnd) {
        DWORD windowPID;
        GetWindowThreadProcessId(hwnd, &windowPID);

        if (windowPID == currentPID && IsWindowVisible(hwnd)) {
            return hwnd;
        }

        hwnd = FindWindowEx(nullptr, hwnd, nullptr, nullptr);
    }

    return nullptr;
}

DWORD WINAPI StealthThread(LPVOID) {
    HWND hwnd = FindMainWindow();
    if (hwnd != nullptr) {
        SetWindowDisplayAffinity(hwnd, WDA_EXCLUDEFROMCAPTURE);
    }
    return 0;
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD reason, LPVOID) {
    if (reason == DLL_PROCESS_ATTACH) {
        DisableThreadLibraryCalls(hModule);
        CreateThread(nullptr, 0, StealthThread, nullptr, 0, nullptr);
    }
    return TRUE;
}
