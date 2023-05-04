DESCRIPTION = "Information command to display key Edge stats"

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
# uname for example comes via coreutils
RDEPENDS:${PN} += "coreutils bash ncurses bc curl gawk findutils jq sed"

RM_WORK_EXCLUDE += "${PN}"

FILES:${PN} = "\
${EDGE_BIN}/edge-info \
"

S = "${WORKDIR}/git"

do_compile() {
	cd ${S}/pe-utils/edge-info
	edge_replace_vars edge-info
}

do_install() {
    install -d ${D}${EDGE_BIN}
    install -m 0755 ${S}/pe-utils/edge-info/edge-info ${D}/${EDGE_BIN}/
}
