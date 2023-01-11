DESCRIPTION = "Tool to convert the development certificate to CBOR formatted object"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCREV = "${EDGE_CORE_VERSION}"

PV="${SRCREV}+git${SRCPV}"
SRC_URI = "git://github.com/PelionIoT/mbed-edge.git;protocol=https;branch=master"

S = "${WORKDIR}/git/edge-tool"

FILES:${PN} = "/edge \
               /edge/mbed \
               /edge/mbed/edge-tool/*"

RDEPENDS:${PN} += "python3-cbor2 python3-cryptography python3-docopt python3-six python3-pyclibrary"

inherit setuptools3 edge_versions

do_install() {
    install -d "${D}/edge/mbed"
    cp -r ${S} ${D}/edge/mbed/edge-tool
}
