
SUMMARY = "C binding automation"
HOMEPAGE = "http://github.com/MatthieuDartiailh/pyclibrary"
AUTHOR = "PyCLibrary Developers <m.dartiailh@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e0779d1fc89e60083c1d63c61507990"

SRC_URI = "https://files.pythonhosted.org/packages/6d/39/161c146a07004b31d80f083412503cbb755143dc5c10d795d36edc77c74f/pyclibrary-0.2.1.tar.gz"
SRC_URI[md5sum] = "9ee972b0c9f0b5569e6f328ebc808dc8"
SRC_URI[sha256sum] = "735b2b42a9015a0cfdd8522b14fb9d0a66a9f790b5f0e948bda90675f54be049"

S = "${WORKDIR}/pyclibrary-0.2.1"

RDEPENDS:${PN} = "python3-future python3-pyparsing"

inherit setuptools3
