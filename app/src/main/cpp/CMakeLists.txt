
cmake_minimum_required(VERSION 3.22.1)

project("freedomtool")

include_directories(include)

link_directories(lib)

add_library(rapidsnark SHARED IMPORTED)
set_target_properties(rapidsnark PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/librapidsnark.so)


add_library(passport-verification-256 SHARED IMPORTED)
set_target_properties(passport-verification-256 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_passportVerificationSHA256.so)


add_library(authv2 SHARED IMPORTED)
set_target_properties(authv2 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_authV2.so)

add_library(credAtomicQuery SHARED IMPORTED)
set_target_properties(credAtomicQuery PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_credentialAtomicQueryMTPV2OnChainVoting.so)

add_library(passport-verification-1 SHARED IMPORTED)
set_target_properties(passport-verification-1 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_passportVerificationSHA1.so)

add_library(voteSMT SHARED IMPORTED)
set_target_properties(voteSMT PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_voteSMT.so)

add_library(${CMAKE_PROJECT_NAME} SHARED
        freedomtool.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log passport-verification-1 credAtomicQuery rapidsnark authv2 passport-verification-256 voteSMT)
