#include "process_inject.h"
#include <iostream>
#include "memory_dumper.h"

const std::string dllStealthPath = "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\target\\native\\stealth.dll";
void applyStealth(const std::string_view str) {

    if (!injectDLLIntoProcess(str, dllStealthPath)) {
        std::cerr << "[-] Stealth injection failed.\n";
    } else {
        std::cout << "[+] Stealth injection succeeded.\n";
    }
}


void applyStealthByPid(const DWORD pid) {
    if (!injectDLLIntoProcessByPid(pid, dllStealthPath)) {
        std::cerr << "[-] Stealth injection failed.\n";
    } else {
        std::cout << "[+] Stealth injection succeeded.\n";
    }
}

void grabMemoryDump(const std::string_view str) {

    injectDLLIntoProcess(str, "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\target\\native\\memory_dumper.dll");

}





