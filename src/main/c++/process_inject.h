#pragma once
#include <string_view>

bool injectDLLIntoProcess(const std::string_view processName, const std::string_view dllPath);
