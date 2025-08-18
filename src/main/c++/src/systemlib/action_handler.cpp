#include "process_inject.h"
#include <iostream>
#include "memory_dumper.h"

constexpr std::string_view dllStealthPath = "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\target\\native\\stealth.dll";

void applyStealth(const std::string_view processName) {

    if (!injectDLLIntoProcess(processName, dllStealthPath)) {
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

/*
 * str is the processname
 */
void applyUnStealth(const std::string_view processName) {

    constexpr std::string_view dllUnStealthPath = "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\target\\native\\unstealth.dll";
    if (!injectDLLIntoProcess(processName, dllUnStealthPath)) {
        std::cerr << "[-] Stealth injection failed.\n";
    } else {
        std::cout << "[+] Stealth injection succeeded.\n";
    }
}



void grabMemoryDump(const std::string_view str) {

    injectDLLIntoProcess(str, "C:\\Users\\justi\\IdeaProjects\\SysOps\\src\\main\\c++\\target\\native\\memory_dumper.dll");

}
