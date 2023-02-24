SUMMARY = "Mbed Cli"
HOMEPAGE = "https://pypi.org/project/mbed-cli/"
AUTHOR = "support@mbed.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

SRC_URI[sha256sum] = "5fe84d54cf1fb32d15153a85af5a4f29646299a7017a74dc638cbe3c1269be52"

RDEPENDS:${PN} = "python3-click python3-requests"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
