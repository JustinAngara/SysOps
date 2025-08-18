// Single-file JNI implementation: no generated header needed.
#include <jni.h>
#include <iostream>
#include <algorithm>
#include <string>
#include "action_handler.h"
#include "process_inject.h"
// Signatures must MATCH the Java class + method names exactly.
// Format: Java_<Class>_<method>
// (No package here because SystemLib is in the default package.)

extern "C" {
    /*
    // void SystemLib.sayHello()
    JNIEXPORT void JNICALL Java_SystemLib_sayHello(JNIEnv*, jobject) {
        std::cout << "Hello from C++ (systemlib.cpp)!" << std::endl;
        std::cout << "What is up bitches?";
    }


    // int SystemLib.add(int a, int b)
    JNIEXPORT jint JNICALL Java_SystemLib_add(JNIEnv*, jobject, jint a, jint b) {
        return a + b;
    }

    JNIEXPORT jstring JNICALL Java_SystemLib_Read(JNIEnv* env, jobject, jstring a) {
        if (!a) return nullptr;
        const char* utf = env->GetStringUTFChars(a, nullptr);
        std::string s(utf);
        env->ReleaseStringUTFChars(a, utf);

        // Example: uppercase
        for (auto& c : s) c = toupper(c);

        std::cout<< s.c_str() << " <-This is c now.";

        return env->NewStringUTF(s.c_str());
    }
    */

    // void SystemLib.applyStealth(str_process_exe);
    JNIEXPORT void JNICALL Java_SystemLib_applyStealth(JNIEnv* env, jobject, jstring processExe) {

        if (!processExe) return;
        const char* utf = env->GetStringUTFChars(processExe, nullptr);
        std::string s(utf);
        env->ReleaseStringUTFChars(processExe, utf);

        std::string str {s.c_str()};
        std::cout << "you have reached over here " << str << "\n";
        applyStealth(str);
    }
    // void SystemLib.applyStealthByPid(int pid)
    JNIEXPORT void JNICALL Java_SystemLib_applyStealthByPid(JNIEnv*, jobject, jint pid) {
        DWORD nativePid = static_cast<DWORD>(pid);
        applyStealthByPid(nativePid);
    }



    // void SystemLib.applyUnStealth(str_process_exe);
    JNIEXPORT void JNICALL Java_SystemLib_applyUnStealth(JNIEnv* env, jobject, jstring processExe) {

        if (!processExe) return;
        const char* utf = env->GetStringUTFChars(processExe, nullptr);
        std::string s(utf);
        env->ReleaseStringUTFChars(processExe, utf);

        std::string str {s.c_str()};
        std::cout << "you have reached over here " << str << "\n";
        applyUnStealth(str);
    }


    // void SystemLib.memoryDumpByProcessName(str_process_exe);
    JNIEXPORT void JNICALL Java_SystemLib_memoryDumpByProcessName(JNIEnv* env, jobject, jstring processExe) {

        const char* utf = env->GetStringUTFChars(processExe, nullptr);
        std::string name(utf);
        env->ReleaseStringUTFChars(processExe, utf);
        grabMemoryDump(name);

    }
} // extern "C"
