//
// Created by justi on 8/17/2025.
//

#ifndef ACTION_HANDLER_H
#define ACTION_HANDLER_H


#include <string_view>
#include "process_inject.h"

void applyStealth(const std::string_view str);
void applyStealthByPid(const DWORD pid);


#endif //ACTION_HANDLER_H
