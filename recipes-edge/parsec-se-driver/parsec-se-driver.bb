
SUMMARY = "Parsec Secure Element Driver"
HOMEPAGE = "https://github.com/parallaxsecond/parsec-se-driver"
LICENSE = "Apache-2.0"

inherit cargo
SRCREV_parsec-se-driver = "08572372c8fecb90eb4318548ee03855956237ba"
SRCREV_mbedtls = "89f040a5c938985c5f30728baed21e49d0846a53"
SRCREV_FORMAT = "parsec-se-driver_mbedtls"
SRC_URI = "git://github.com/parallaxsecond/parsec-se-driver.git;protocol=https;branch=main;name=parsec-se-driver \
           git://github.com/Mbed-TLS/mbedtls.git;protocol=https;destsuffix=mbedtls;name=mbedtls;branch=mbedtls-2.28"

PARSEC_version = "0.6.0"
PV = "${PARSEC_version}+git${SRCREV_parsec-se-driver}"

S = "${WORKDIR}/git"
S_MBEDTLS = "${WORKDIR}/mbedtls"

TOOLCHAIN = "clang"

do_configure[postfuncs] = "0"
do_compile:prepend() {
    export MBEDTLS_INCLUDE_DIR="${S_MBEDTLS}/include"
}

do_install() {
    install -d "${D}/${libdir}"
    install -m 755 "${B}/target/${CARGO_TARGET_SUBDIR}/libparsec_se_driver.a" "${D}/${libdir}"
}

include parsec-se-driver.inc
