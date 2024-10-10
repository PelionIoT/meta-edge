DESCRIPTION = "Izuma Edge-Terminal for web based ssh sessions"

LICENSE = "Apache-2.0"

GO_IMPORT = "github.com/PelionIoT/pe-terminal"

# avoid the `-linkshared` option in this recipe as it causes a panic
GO_LINKSHARED=""
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

inherit pkgconfig systemd go gitpkgv edge

RT_SERVICE_FILE = "edge-terminal.service"
PR = "r1"

SRC_URI = "git://${GO_IMPORT};protocol=https;branch=master \
file://${RT_SERVICE_FILE} \
file://${BPN}-watcher.service \
file://${BPN}.path \
file://config.json \
"

LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "v1.2.0"
PV = "${SRCREV}+git${SRCPV}"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${RT_SERVICE_FILE} \
${PN}-watcher.service \
${PN}.path"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN} += "\
    ${systemd_system_unitdir}/${PN}-watcher.service\
    ${systemd_system_unitdir}/${PN}.path\
    ${systemd_system_unitdir}/${RT_SERVICE_FILE}\
    ${EDGE_BIN}/edge-terminal\
    ${EDGE_ETC}/edge-terminal/config.json\
    "

do_compile() {
  # Try not to have any read-only files in the build area (they make cleanup difficult if build fails)
  ${GO} env -w GOFLAGS=-modcacherw
  cd src/${GO_IMPORT}
  ${GO} build
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
  cd ${S}/../

  edge_replace_vars ${RT_SERVICE_FILE} ${BPN}.path
}

do_install() {

    install -d ${D}${EDGE_BIN}
    install -d ${D}${EDGE_ETC}/edge-terminal
    install -d ${D}${systemd_system_unitdir}

    install -m 0644 ${S}/../${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
    install -m 0644 ${S}/../${PN}.path ${D}${systemd_system_unitdir}/${PN}.path
    install -m 0644 ${S}/../${PN}-watcher.service ${D}${systemd_system_unitdir}/${PN}-watcher.service
    install -m 0755 ${S}/src/${GO_IMPORT}/pe-terminal ${D}${EDGE_BIN}/edge-terminal
    install -m 0644 ${S}/../config.json ${D}${EDGE_ETC}/edge-terminal/config.json

}
