// src/unstealth/unstealth.cpp
#include <windows.h>
#include <tlhelp32.h>

static DWORD GetCurrentPid() {
    return GetCurrentProcessId();
}

static BOOL CALLBACK EnumWindowsProc(HWND hwnd, LPARAM lParam) {
    DWORD pid = 0;
    GetWindowThreadProcessId(hwnd, &pid);
    if (pid == static_cast<DWORD>(lParam)) {
        // Only accept visible top-level windows
        if (GetWindow(hwnd, GW_OWNER) == nullptr && IsWindowVisible(hwnd)) {
            SetLastError(ERROR_SUCCESS);
            SetWindowLongPtr(hwnd, GWLP_USERDATA, 1); // mark
            return FALSE; // stop enumeration
        }
    }
    return TRUE;
}

static HWND FindMainWindowForCurrentProcess() {
    // We’ll store found HWND temporarily using GWLP_USERDATA trick
    // Enumerate, tag the first match, then scan again to fetch it.
    EnumWindows(EnumWindowsProc, static_cast<LPARAM>(GetCurrentPid()));

    HWND hwnd = nullptr;
    EnumWindows([](HWND h, LPARAM lp) -> BOOL {
        if (GetWindowLongPtr(h, GWLP_USERDATA) == 1) {
            *reinterpret_cast<HWND*>(lp) = h;
            SetWindowLongPtr(h, GWLP_USERDATA, 0);
            return FALSE;
        }
        return TRUE;
    }, reinterpret_cast<LPARAM>(&hwnd));

    return hwnd;
}

DWORD WINAPI UnstealthThread(LPVOID) {
    HWND hwnd = FindMainWindowForCurrentProcess();
    if (hwnd) {
        // Remove exclusion from capture
        SetWindowDisplayAffinity(hwnd, WDA_NONE);
    }
    return 0;
}

BOOL APIENTRY DllMain(HMODULE h, DWORD reason, LPVOID) {
    if (reason == DLL_PROCESS_ATTACH) {
        DisableThreadLibraryCalls(h);
        CreateThread(nullptr, 0, UnstealthThread, nullptr, 0, nullptr);
    }
    return TRUE;
}
