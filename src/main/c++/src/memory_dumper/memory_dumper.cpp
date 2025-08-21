#include <windows.h>
#include <tlhelp32.h>
#include <psapi.h>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <shlobj.h>
#include <iomanip>

#pragma comment(lib, "psapi.lib")

class ReadableMemoryScanner {
private:
    std::string ToHex(DWORD_PTR value) {
        std::ostringstream oss;
        oss << std::hex << std::uppercase << value;
        return oss.str();
    }

    std::string GetReadableValue(const BYTE* buffer, SIZE_T size, DWORD_PTR address) {
        // Try string first (most readable)
        std::string str;
        for (SIZE_T i = 0; i < size && i < 50; i++) {
            char c = buffer[i];
            if (c == 0) break; // null terminator
            if (c >= 32 && c <= 126) {
                str += c;
            } else {
                break; // not a valid string
            }
        }

        // If we found a decent string (3+ chars), return it
        if (str.length() >= 3) {
            return "\"" + str + "\"";
        }

        // Try as integer (4 bytes)
        if (size >= 4) {
            DWORD intVal = *(DWORD*)buffer;
            if (intVal > 0 && intVal < 1000000000) { // reasonable integer range
                return std::to_string(intVal);
            }
        }

        // Try as float (4 bytes)
        if (size >= 4) {
            float floatVal = *(float*)buffer;
            if (!isnan(floatVal) && !isinf(floatVal) &&
                floatVal > -1000000 && floatVal < 1000000 && floatVal != 0) {
                std::ostringstream oss;
                oss << std::fixed << std::setprecision(2) << floatVal;
                return oss.str() + "f";
            }
        }

        // Try as pointer
        if (size >= sizeof(void*)) {
            DWORD_PTR ptrVal = *(DWORD_PTR*)buffer;
            if (ptrVal > 0x10000 && ptrVal < 0x7FFFFFFFFFFF) {
                return "ptr->" + ToHex(ptrVal);
            }
        }

        // Return raw bytes if nothing else works
        std::string hexBytes;
        for (SIZE_T i = 0; i < min(size, (SIZE_T)8); i++) {
            std::ostringstream oss;
            oss << std::hex << std::uppercase << std::setfill('0') << std::setw(2) << (int)buffer[i];
            hexBytes += oss.str() + " ";
        }
        return hexBytes;
    }

    std::string GetDownloadPath() {
        char path[MAX_PATH];
        if (SUCCEEDED(SHGetFolderPathA(NULL, CSIDL_PROFILE, NULL, 0, path))) {
            return std::string(path) + "\\Downloads\\";
        }
        return "C:\\temp\\";
    }

    std::string GetProcessName() {
        char processName[MAX_PATH];
        if (GetModuleBaseNameA(GetCurrentProcess(), NULL, processName, MAX_PATH)) {
            return std::string(processName);
        }
        return "unknown";
    }

public:
    void ScanMemory() {
        std::string filename = GetProcessName() + "_readable_memory.txt";
        std::string fullPath = GetDownloadPath() + filename;

        std::ofstream file(fullPath);
        if (!file.is_open()) return;

        HANDLE hProcess = GetCurrentProcess();
        SYSTEM_INFO sysInfo;
        GetSystemInfo(&sysInfo);

        LPVOID address = sysInfo.lpMinimumApplicationAddress;

        while (address < sysInfo.lpMaximumApplicationAddress) {
            MEMORY_BASIC_INFORMATION mbi;
            if (VirtualQuery(address, &mbi, sizeof(mbi)) != sizeof(mbi)) {
                address = (LPVOID)((DWORD_PTR)address + sysInfo.dwPageSize);
                continue;
            }

            if (mbi.State == MEM_COMMIT &&
                !(mbi.Protect & PAGE_NOACCESS) &&
                !(mbi.Protect & PAGE_GUARD)) {

                ScanRegion(file, hProcess, mbi.BaseAddress, mbi.RegionSize);
            }

            address = (LPVOID)((DWORD_PTR)mbi.BaseAddress + mbi.RegionSize);
        }

        file.close();
    }

private:
    void ScanRegion(std::ofstream& file, HANDLE hProcess, LPVOID baseAddr, SIZE_T regionSize) {
        const SIZE_T CHUNK_SIZE = 4096;
        std::vector<BYTE> buffer(CHUNK_SIZE);

        for (SIZE_T offset = 0; offset < regionSize; offset += CHUNK_SIZE) {
            SIZE_T bytesToRead = min(CHUNK_SIZE, regionSize - offset);
            SIZE_T bytesRead = 0;

            LPVOID currentAddr = (LPVOID)((DWORD_PTR)baseAddr + offset);

            if (ReadProcessMemory(hProcess, currentAddr, buffer.data(), bytesToRead, &bytesRead)) {

                // Scan for strings first (every byte)
                for (SIZE_T i = 0; i < bytesRead - 3; i++) {
                    if (buffer[i] >= 32 && buffer[i] <= 126) { // printable char
                        std::string str;
                        SIZE_T j = i;
                        while (j < bytesRead && buffer[j] >= 32 && buffer[j] <= 126) {
                            str += (char)buffer[j];
                            j++;
                        }

                        if (str.length() >= 4) { // found a string
                            DWORD_PTR addr = (DWORD_PTR)currentAddr + i;
                            file << ToHex(addr) << "\t\"" << str << "\"\n";
                            i = j - 1; // skip past this string
                        }
                    }
                }

                // Scan for other data types (4-byte aligned)
                for (SIZE_T i = 0; i <= bytesRead - 4; i += 4) {
                    DWORD_PTR addr = (DWORD_PTR)currentAddr + i;

                    // Skip if we already found a string here
                    bool isStringLocation = false;
                    for (int k = 0; k < 4; k++) {
                        if (buffer[i + k] >= 32 && buffer[i + k] <= 126) {
                            isStringLocation = true;
                            break;
                        }
                    }

                    if (!isStringLocation) {
                        std::string readableValue = GetReadableValue(&buffer[i], bytesRead - i, addr);

                        // Only output if it's interesting (not just hex bytes)
                        if (readableValue.find("00 00 00") == std::string::npos &&
                            readableValue != "0" &&
                            !readableValue.empty()) {
                            file << ToHex(addr) << "\t" << readableValue << "\n";
                        }
                    }
                }
            }
        }
    }
};

DWORD WINAPI ScanThread(LPVOID lpParam) {
    Sleep(1000);

    try {
        ReadableMemoryScanner scanner;
        scanner.ScanMemory();
    }
    catch (...) {
    }

    return 0;
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved) {
    switch (ul_reason_for_call) {
        case DLL_PROCESS_ATTACH:
            DisableThreadLibraryCalls(hModule);
            CreateThread(NULL, 0, ScanThread, NULL, 0, NULL);
            break;
        case DLL_THREAD_ATTACH:
        case DLL_THREAD_DETACH:
        case DLL_PROCESS_DETACH:
            break;
    }
    return TRUE;
}