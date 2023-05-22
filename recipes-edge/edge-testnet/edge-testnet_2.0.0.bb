DESCRIPTION = "Edge-testnet - test network for firewall blocks on Izuma Edge"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/pe-utils/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
git://git@github.com/PelionIoT/pe-utils.git;protocol=https;name=pe-utils;destsuffix=git/pe-utils;branch=master \
"
SRCREV_pe-utils = "${PE_UTILS_VERSION}"

inherit pkgconfig gitpkgv edge edge_versions

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"

DEPENDS = ""
RDEPENDS:${PN} += "busybox openssl"

RM_WORK_EXCLUDE += "${PN}"

FILES:${PN} = "\
${EDGE_BIN}/edge-testnet \
${EDGE_BIN}/credentials/bootstrap.pem \
${EDGE_BIN}/credentials/device01_cert.pem \
${EDGE_BIN}/credentials/device01_key.pem \
${EDGE_BIN}/credentials/lwm2m.pem \
"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${EDGE_BIN}
	install -d ${D}${EDGE_BIN}/credentials
	install -m 0777 ${S}/pe-utils/fw-tools/edge-testnet ${D}/${EDGE_BIN}/
	install -m 0755 ${S}/pe-utils/fw-tools/credentials/bootstrap.pem ${D}/${EDGE_BIN}/credentials
	install -m 0755 ${S}/pe-utils/fw-tools/credentials/device01_cert.pem ${D}/${EDGE_BIN}/credentials
	install -m 0755 ${S}/pe-utils/fw-tools/credentials/device01_key.pem ${D}/${EDGE_BIN}/credentials
	install -m 0755 ${S}/pe-utils/fw-tools/credentials/lwm2m.pem ${D}/${EDGE_BIN}/credentials
}
