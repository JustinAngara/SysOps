#include "process_inject.h"
#include <iostream>

void applyStealth(const std::string_view str) {
    const std::string dllPath = "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\stealth.dll";

    if (!injectDLLIntoProcess(str, dllPath)) {
        std::cerr << "[-] Stealth injection failed.\n";
    } else {
        std::cout << "[+] Stealth injection succeeded.\n";
    }
}
