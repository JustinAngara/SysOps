#pragma once
#include <string_view>
#include <windows.h>



bool injectDLLIntoProcess(const std::string_view processName, const std::string_view dllPath);
bool injectDLLIntoProcessByPid(DWORD pid, const std::string_view dllPath);