#include <windows.h>
#include <tlhelp32.h>
#include <string_view>
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

void applyStealth(const std::string_view str) {
    DWORD pid = findProcessIdByName(str);
    if (pid == 0) {
        std::cerr << "[-] Process not found: " << str << "\n";
        return;
    }

    std::cout << "[*] Found process '" << str << "' with PID " << pid << "\n";

    // Path to the DLL to inject
    const char* dllPath = "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\stealth.dll"; // TODO: replace this with your actual DLL path
    size_t dllLen = strlen(dllPath) + 1;

    HANDLE hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);
    if (!hProcess) {
        std::cerr << "[-] Failed to open process.\n";
        return;
    }

    // Allocate space for the DLL path in the remote process
    LPVOID remoteMem = VirtualAllocEx(hProcess, nullptr, dllLen, MEM_COMMIT, PAGE_READWRITE);
    if (!remoteMem) {
        std::cerr << "[-] Failed to allocate memory in target process.\n";
        CloseHandle(hProcess);
        return;
    }

    // Write the DLL path to the remote process
    WriteProcessMemory(hProcess, remoteMem, dllPath, dllLen, nullptr);

    // Get the address of LoadLibraryA
    LPVOID loadLibAddr = GetProcAddress(GetModuleHandleA("kernel32.dll"), "LoadLibraryA");

    // Create remote thread to call LoadLibraryA(dllPath)
    HANDLE hThread = CreateRemoteThread(hProcess, nullptr, 0,
        (LPTHREAD_START_ROUTINE)loadLibAddr,
        remoteMem, 0, nullptr);

    if (!hThread) {
        std::cerr << "[-] Failed to create remote thread.\n";
        VirtualFreeEx(hProcess, remoteMem, 0, MEM_RELEASE);
        CloseHandle(hProcess);
        return;
    }

    std::cout << "[+] DLL injected successfully.\n";

    // Wait for the thread to finish and clean up
    WaitForSingleObject(hThread, INFINITE);
    VirtualFreeEx(hProcess, remoteMem, 0, MEM_RELEASE);
    CloseHandle(hThread);
    CloseHandle(hProcess);
}
