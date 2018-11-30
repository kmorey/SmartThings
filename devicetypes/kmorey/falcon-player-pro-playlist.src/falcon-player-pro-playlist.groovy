/**
 *  Falcon Player Pro Playlist
 */
metadata {
	definition (name: "Falcon Player Pro Playlist", namespace: "kmorey", author: "Kevin Morey", parent: "kmorey:Falcon Player Pro Controller") {
		capability "Switch"
        capability "Actuator"
        capability "Sensor"
	}

	simulator {
	}

	tiles {
    	standardTile("switch", "device.switch", width: 3, height: 3, canChangeIcon: true) {
            state "off", label: '${currentValue}', action: "switch.on",
                  icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off",
                  icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
        }
	}
}

def on() {
    parent.childOn(device.deviceNetworkId)
}

def off() {
    parent.childOff(device.deviceNetworkId)
}