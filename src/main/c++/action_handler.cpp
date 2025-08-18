#include "process_inject.h"
#include <iostream>

const std::string dllPath = "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\stealth.dll";
void applyStealth(const std::string_view str) {

    if (!injectDLLIntoProcess(str, dllPath)) {
        std::cerr << "[-] Stealth injection failed.\n";
    } else {
        std::cout << "[+] Stealth injection succeeded.\n";
    }
}


void applyStealthByPid(const DWORD pid) {
    if (!injectDLLIntoProcessByPid(pid, dllPath)) {
        std::cerr << "[-] Stealth injection failed.\n";
    } else {
        std::cout << "[+] Stealth injection succeeded.\n";
    }
}

