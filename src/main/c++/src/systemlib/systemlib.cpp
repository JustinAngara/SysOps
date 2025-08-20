// Single-file JNI implementation: no generated header required (though recommended).
#include <jni.h>
#include <windows.h>          // for DWORD
#include <iostream>
#include <algorithm>
#include <string>

#include "action_handler.h"
#include "process_inject.h"

// Java side:
// package com.sysops.functions;
// public class SystemLib {
//   public native void applyStealth(String processExe);
//   public native void applyStealthByPid(int pid);
//   public native void applyUnStealth(String processExe);
//   public native void memoryDumpByProcessName(String processExe);
// }

extern "C" {

JNIEXPORT void JNICALL
Java_com_sysops_functions_SystemLib_applyStealth(JNIEnv* env, jobject /*thiz*/, jstring processExe) {
    if (!processExe) return;
    const char* utf = env->GetStringUTFChars(processExe, nullptr);
    std::string name(utf ? utf : "");
    env->ReleaseStringUTFChars(processExe, utf);

    std::cout << "[JNI] applyStealth(\"" << name << "\")\n";
    applyStealth(name);
}

JNIEXPORT void JNICALL
Java_com_sysops_functions_SystemLib_applyStealthByPid(JNIEnv* /*env*/, jobject /*thiz*/, jint pid) {
    DWORD nativePid = static_cast<DWORD>(pid);
    std::cout << "[JNI] applyStealthByPid(" << nativePid << ")\n";
    applyStealthByPid(nativePid);
}

JNIEXPORT void JNICALL
Java_com_sysops_functions_SystemLib_applyUnStealth(JNIEnv* env, jobject /*thiz*/, jstring processExe) {
    if (!processExe) return;
    const char* utf = env->GetStringUTFChars(processExe, nullptr);
    std::string name(utf ? utf : "");
    env->ReleaseStringUTFChars(processExe, utf);

    std::cout << "[JNI] applyUnStealth(\"" << name << "\")\n";
    applyUnStealth(name);
}

JNIEXPORT void JNICALL
Java_com_sysops_functions_SystemLib_memoryDumpByProcessName(JNIEnv* env, jobject /*thiz*/, jstring processExe) {
    if (!processExe) return;
    const char* utf = env->GetStringUTFChars(processExe, nullptr);
    std::string name(utf ? utf : "");
    env->ReleaseStringUTFChars(processExe, utf);

    std::cout << "[JNI] memoryDumpByProcessName(\"" << name << "\")\n";
    grabMemoryDump(name);
}

} // extern "C"
