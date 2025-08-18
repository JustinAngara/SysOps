#include "process_inject.h"
#include <windows.h>
#include <tlhelp32.h>
#include <string>
#include <iostream>

DWORD findProcessIdByName(std::string_view name) {
    PROCESSENTRY32 pe32 = {};
    pe32.dwSize = sizeof(PROCESSENTRY32);

    HANDLE hSnapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    if (hSnapshot == INVALID_HANDLE_VALUE)
        return 0;

    DWORD pid = 0;
    if (Process32First(hSnapshot, &pe32)) {
        do {
            if (_stricmp(pe32.szExeFile, name.data()) == 0) {
                pid = pe32.th32ProcessID;
                break;
            }
        } while (Process32Next(hSnapshot, &pe32));
    }

    CloseHandle(hSnapshot);
    return pid;
}

bool injectDLLIntoProcess(const std::string_view processName, const std::string_view dllPath) {
    DWORD pid = findProcessIdByName(processName);
    if (pid == 0) {
        std::cerr << "[-] Could not find process: " << processName << "\n";
        return false;
    }

    std::cout << "[*] Found process '" << processName << "' with PID " << pid << "\n";

    HANDLE hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);
    if (!hProcess) {
        std::cerr << "[-] Failed to open process.\n";
        return false;
    }

    LPVOID remoteMem = VirtualAllocEx(hProcess, nullptr, dllPath.length() + 1, MEM_COMMIT, PAGE_READWRITE);
    if (!remoteMem) {
        std::cerr << "[-] Failed to allocate memory in target process.\n";
        CloseHandle(hProcess);
        return false;
    }

    WriteProcessMemory(hProcess, remoteMem, dllPath.data(), dllPath.length() + 1, nullptr);

    LPVOID loadLibAddr = GetProcAddress(GetModuleHandleA("kernel32.dll"), "LoadLibraryA");
    if (!loadLibAddr) {
        std::cerr << "[-] Failed to get LoadLibraryA address.\n";
        VirtualFreeEx(hProcess, remoteMem, 0, MEM_RELEASE);
        CloseHandle(hProcess);
        return false;
    }

    HANDLE hThread = CreateRemoteThread(hProcess, nullptr, 0,
        (LPTHREAD_START_ROUTINE)loadLibAddr,
        remoteMem, 0, nullptr);

    if (!hThread) {
        std::cerr << "[-] Failed to create remote thread.\n";
        VirtualFreeEx(hProcess, remoteMem, 0, MEM_RELEASE);
        CloseHandle(hProcess);
        return false;
    }

    WaitForSingleObject(hThread, INFINITE);

    VirtualFreeEx(hProcess, remoteMem, 0, MEM_RELEASE);
    CloseHandle(hThread);
    CloseHandle(hProcess);

    std::cout << "[+] DLL injected successfully.\n";
    return true;
}
