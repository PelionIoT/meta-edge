DESCRIPTION = "Identity utility used by the Izuma Edge"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/pe-utils/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
git://git@github.com/PelionIoT/pe-utils.git;protocol=https;name=pe-utils;destsuffix=git/pe-utils;branch=master \
file://wait-for-edge-identity.service \
"

SRCREV_pe-utils = "${PE_UTILS_VERSION}"

inherit pkgconfig gitpkgv systemd edge edge_versions

#INHIBIT_PACKAGE_STRIP = "1"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "wait-for-edge-identity.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"

DEPENDS = ""
RDEPENDS:${PN} += " bash curl jq"

RM_WORK_EXCLUDE += "${PN}"

FILES:${PN} = "\
    ${EDGE_BIN} \
    ${EDGE_BIN}/developer_identity \
    ${EDGE_BIN}/generate-identity.sh \
    ${EDGE_BIN}/developer_identity/create-dev-identity.sh \
    ${EDGE_BIN}/developer_identity/radioProfile.template.json \
    ${EDGE_BIN}/developer_identity/VERSION \
    ${systemd_system_unitdir}/wait-for-edge-identity.service \
"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${EDGE_BIN}
    install -d ${D}${EDGE_BIN}/developer_identity
	install -m 0755 ${S}/pe-utils/identity-tools/generate-identity.sh ${D}${EDGE_BIN}/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/create-dev-identity.sh ${D}${EDGE_BIN}/developer_identity/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/radioProfile.template.json ${D}${EDGE_BIN}/developer_identity/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/VERSION ${D}${EDGE_BIN}/developer_identity/

	# Install systemd units
	install -d ${D}${systemd_system_unitdir}
	install -m 644 ${WORKDIR}/wait-for-edge-identity.service ${D}${systemd_system_unitdir}/wait-for-edge-identity.service
}



