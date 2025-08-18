// Single-file JNI implementation: no generated header needed.
#include <jni.h>
#include <iostream>
#include <algorithm>
#include <string>

// Signatures must MATCH the Java class + method names exactly.
// Format: Java_<Class>_<method>
// (No package here because SystemLib is in the default package.)

extern "C" {

    // void SystemLib.sayHello()
    JNIEXPORT void JNICALL Java_SystemLib_sayHello(JNIEnv*, jobject) {
        std::cout << "Hello from C++ (systemlib.cpp)!" << std::endl;
        std::cout << "What is up bitches?";

    }

    // int SystemLib.add(int a, int b)
    JNIEXPORT jint JNICALL Java_SystemLib_add(JNIEnv*, jobject, jint a, jint b) {
        return a + b;
    }

    // String SystemLib.reverse(String s)
    JNIEXPORT jstring JNICALL Java_SystemLib_reverse(JNIEnv* env, jobject, jstring js) {
        if (!js) return env->NewStringUTF("");
        const char* utf = env->GetStringUTFChars(js, nullptr);
        std::string s(utf ? utf : "");
        env->ReleaseStringUTFChars(js, utf);
        std::reverse(s.begin(), s.end());
        return env->NewStringUTF(s.c_str());
    }

} // extern "C"
