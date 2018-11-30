/**
 *  Falcon Player Pro Controller
 */
metadata {
    definition (
        name: "Falcon Player Pro Controller", 
        namespace: "kmorey", 
        author: "Kevin Morey",
        parent: "kmorey:Falcon Player Pro"
    ) {
		capability "Switch"
        capability "Refresh"
        capability "Sensor"
        
        command "playlistOn"
        command "playlistOff"
        command "refresh"
	}

	simulator {
	}

	tiles (scale: 2) {
    	standardTile("status", "device.switch", width: 3, height: 3, canChangeIcon: true) {
            state "off", label: 'OFF', icon: "st.Seasonal Winter.seasonal-winter-001", backgroundColor:"#ffffff"
            state "on", label: 'ON', icon: "st.Seasonal Winter.seasonal-winter-001", backgroundColor:"#00a0dc"
        }
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 3, height: 3) {
			state "refresh", label:"", action:"refresh.refresh", icon:"st.secondary.refresh", defaultState: true
		}
        valueTile("playlist", "device.playlist", decoration: "flat", width: 6, height: 2) {
        	state "val", label: '${currentValue}', icon: "st.switches.light.on"
        }
        
        childDeviceTiles("buttons")
        
        main("status")
	}
}

def installed() {
    refresh()
}

def dniToPlaylist(dni) {
	return dni.split('-')[1]
}

// parse events into attributes
def parse(String description) {}

// handle commands
def playlistOn(dni = null) {
    if (dni == null) {
        dni = getChildDevices()[0].deviceNetworkId
    }
    def playlist = dniToPlaylist(dni)
    parent.startPlaylist(playlist)
    
    sendEvent(name: "switch", value: "on", isStateChange: true)
    updateState(playlist)
}

def playlistOff() {
	parent.stopEverything()
    
    sendEvent(name: "switch", value: "off", isStateChange: true)
    updateState(null)
}

def refresh() {
	log.debug "Executing 'refresh'"
    createFppPlaylists()
    getStatus()
}

void childOn(String dni) {
	playlistOn(dni)
}

void childOff(String dni) {
	playlistOff()
}

def createFppPlaylists() {
	parent.fetchPlaylists()
}

def getStatus() {
    parent.getStatus()
}

void getPlaylistsHandler(playlists) {
	log.debug "Entered getPlaylistsHandler()..."
    def children = getChildDevices()
    
    for (playlist in playlists) {
        def label = playlist.replace("_", " ")
        def childId = "${device.deviceNetworkId}-${playlist}"
        if (children.find { it.deviceNetworkId == childId } == null) {
        	addChildDevice("Falcon Player Pro Playlist", childId, null, [completedSetup: true, label: "${playlist}", isComponent: true, componentName: "playlist${playlist}", componentLabel: "${label}"])
        }
    }
    
    // TODO: remove playlist devices that are no longer in FPP
}

void getStatusHandler(status) {
    log.debug "got status current playlist = ${status.currentPlaylist}"
    
    updateState(status.currentPlaylist)
}

void updateState(activePlaylist) {
    sendEvent(name: "playlist", value: getPlaylistLabel(activePlaylist), isStateChange: true)

    getChildDevices().each {
        def playlist = dniToPlaylist(it.deviceNetworkId)
        if (playlist == activePlaylist) {
            it.sendEvent(name: "switch", value: "on", isStateChange: true)
        }
        else {
            it.sendEvent(name: "switch", value: "off", isStateChange: true)
        }
    }
}

def getPlaylistLabel(playlist) {
	if (playlist == null) { return 'Nothing' }
    return playlist.replace('_', ' ')
}