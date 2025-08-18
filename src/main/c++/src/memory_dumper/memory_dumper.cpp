// memory_dumper.cpp
#include <windows.h>
#include <tlhelp32.h>
#include <psapi.h>
#include <fstream>
#include <vector>
#include <string>

bool DumpProcessMemory(const std::string& processName, const std::string& outPath) {
    DWORD pid = 0;

    HANDLE snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    PROCESSENTRY32 pe = { sizeof(pe) };

    if (Process32First(snapshot, &pe)) {
        do {
            if (_stricmp(pe.szExeFile, processName.c_str()) == 0) {
                pid = pe.th32ProcessID;
                break;
            }
        } while (Process32Next(snapshot, &pe));
    }
    CloseHandle(snapshot);

    if (pid == 0) return false;

    HANDLE hProcess = OpenProcess(PROCESS_VM_READ | PROCESS_QUERY_INFORMATION, FALSE, pid);
    if (!hProcess) return false;

    SYSTEM_INFO sysInfo;
    GetSystemInfo(&sysInfo);

    std::ofstream dumpFile(outPath, std::ios::binary);
    if (!dumpFile) return false;

    MEMORY_BASIC_INFORMATION mbi;
    LPBYTE addr = (LPBYTE)sysInfo.lpMinimumApplicationAddress;

    while (addr < sysInfo.lpMaximumApplicationAddress) {
        if (VirtualQueryEx(hProcess, addr, &mbi, sizeof(mbi)) == sizeof(mbi)) {
            if (mbi.State == MEM_COMMIT && (mbi.Type == MEM_PRIVATE || mbi.Type == MEM_IMAGE)) {
                std::vector<char> buffer(mbi.RegionSize);
                SIZE_T bytesRead;
                if (ReadProcessMemory(hProcess, addr, buffer.data(), mbi.RegionSize, &bytesRead)) {
                    dumpFile.write(buffer.data(), bytesRead);
                }
            }
            addr += mbi.RegionSize;
        } else {
            break;
        }
    }

    CloseHandle(hProcess);
    dumpFile.close();
    return true;
}
