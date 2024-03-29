DESCRIPTION = "maestro is a runtime / container manager for deviceOS"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

GO_IMPORT = "github.com/PelionIoT/maestro"

PROVIDES += " maestro "
RPROVIDES:${PN} += " maestro "

DEPENDS = "libuv"
RDEPENDS:${PN} += "bash"

# avoid the `-linkshared` option in this recipe as it causes a panic
GO_LINKSHARED=""

inherit pkgconfig systemd go gitpkgv edge

RT_SERVICE_FILE = "maestro.service"
PR = "r0"

SRC_URI = "git://${GO_IMPORT};protocol=https;branch=master \
file://${RT_SERVICE_FILE} \
file://maestro-watcher.service \
file://maestro-watcher.path \
file://maestro-config.yaml \
"

SRCREV = "v3.0.0"
PV = "${SRCPV}"


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "maestro.service \
maestro-watcher.path \
maestro-watcher.service"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILESEXTRAPATHS:prepend := "${THISDIR}/maestro:"
FILES:${PN} += "\
    ${EDGE_BIN}/maestro \
    ${EDGE_CONFIG}/maestro-config.yaml\
    ${EDGE_LOG}\
    ${systemd_system_unitdir}/maestro.service\
    ${systemd_system_unitdir}/maestro-watcher.service\
    ${systemd_system_unitdir}/maestro-watcher.path\
    "
do_compile[network] = "1"
do_compile() {
  # Try not to have any read-only files in the build area (they make cleanup difficult if build fails)
  ${GO} env -w GOFLAGS=-modcacherw
  cd src/${GO_IMPORT}
  ${GO} build -o bin/maestro maestro/main.go
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
  cd ${S}/../

  edge_replace_vars ${RT_SERVICE_FILE} maestro-watcher.path
}

do_install:append() {
    # Maestro configuration management
    install -d ${D}/${EDGE_CONFIG}
    install -m 0644 ${WORKDIR}/maestro-config.yaml ${D}/${EDGE_CONFIG}/maestro-config.yaml
}

do_install() {

  install -d ${D}${EDGE_BIN}
  install -d ${D}${systemd_system_unitdir}
  install -d ${D}/${bindir}
  install -d ${D}/${libdir}
  install -d ${D}/${EDGE_LOG}
  install -m 0755 ${S}/src/${GO_IMPORT}/bin/maestro ${D}${EDGE_BIN}/maestro
  ln -sf /${EDGE_BIN}/maestro ${D}/${bindir}/maestro

  install -m 0644 ${S}/../${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
  install -m 0644 ${S}/../maestro-watcher.path ${D}${systemd_system_unitdir}/maestro-watcher.path
  install -m 0644 ${S}/../maestro-watcher.service ${D}${systemd_system_unitdir}/maestro-watcher.service

}
