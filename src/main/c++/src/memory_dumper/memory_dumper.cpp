// src/memory_dumper/memory_dumper.cpp
#include <windows.h>
#include <vector>
#include <fstream>

static void dumpAllMemoryOfCurrentProcess(const char* outPath) {
    HANDLE hProc = GetCurrentProcess();

    SYSTEM_INFO si{};
    GetSystemInfo(&si);

    std::ofstream out(outPath, std::ios::binary);
    if (!out) return;

    MEMORY_BASIC_INFORMATION mbi{};
    BYTE* addr = static_cast<BYTE*>(si.lpMinimumApplicationAddress);

    while (addr < static_cast<BYTE*>(si.lpMaximumApplicationAddress)) {
        if (VirtualQuery(addr, &mbi, sizeof(mbi)) != sizeof(mbi)) {
            addr += 0x1000;
            continue;
        }

        bool readable =
            (mbi.State == MEM_COMMIT) &&
            !(mbi.Protect & (PAGE_NOACCESS | PAGE_GUARD));

        if (readable) {
            std::vector<char> buffer(mbi.RegionSize);
            SIZE_T bytesRead = 0;
            if (ReadProcessMemory(
                    hProc,
                    mbi.BaseAddress,              // LPCVOID
                    buffer.data(),                // LPVOID
                    buffer.size(),                // SIZE_T
                    &bytesRead)) {                // SIZE_T*
                out.write(buffer.data(), static_cast<std::streamsize>(bytesRead));
                    }
        }

        addr = static_cast<BYTE*>(mbi.BaseAddress) + mbi.RegionSize;
    }
}

DWORD WINAPI DumpThread(LPVOID) {
    dumpAllMemoryOfCurrentProcess("C:\\temp\\dump.bin");
    return 0;
}

BOOL APIENTRY DllMain(HMODULE h, DWORD reason, LPVOID) {
    if (reason == DLL_PROCESS_ATTACH) {
        DisableThreadLibraryCalls(h);
        CreateThread(nullptr, 0, DumpThread, nullptr, 0, nullptr);
    }
    return TRUE;
}
