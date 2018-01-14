/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * TODO: fetch all data values in one request!
 */
preferences {
    input name: "deviceId", type: "text", title: "Device ID", required: true
    input name: "token", type: "password", title: "Access Token", required: true
    input name: "temp1Var", type: "text", title: "Temp name 1", required: true, defaultValue: "temp1"
    input name: "temp2Var", type: "text", title: "Temp name 2", required: true, defaultValue: "temp2"
}
metadata {
	definition (name: "Particle.io Temperature Sensor", namespace: "eirikluhr", author: "Eirik Lühr") {
		capability "Sensor"
			capability "Temperature Measurement"
    	    capability "Polling"
        	capability "Refresh"
        	attribute "temp1", "number"
        	attribute "temp2", "number"
	}

	// simulator metadata
	simulator {
		for (int i = 0; i <= 35; i += 5) {
			status "${i}": "temp1:$i"
		}
	}

	// UI tile definitions
	tiles {
		valueTile("temp1", "device.temp1", width: 1, height: 1) {
	        //state "temp1", label:'${currentValue}°', icon:"st.weather.weather2"
			state("temp1", label:'${currentValue}°',
				backgroundColors:[
					[value: 5, color: "#153591"],
					[value: 10, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 20, color: "#44b621"],
					[value: 25, color: "#f1d801"],
					[value: 30, color: "#d04e00"],
					[value: 35, color: "#bc2323"]
				],
                icon:"st.Weather.weather2"
			)
		}
		valueTile("temp2", "device.temp2", width: 1, height: 1) {
	        //state "temp2", label:'${currentValue}°', icon:"st.weather.weather2"
			state("temp2", label:'${currentValue}°',
				backgroundColors:[
					[value: 5, color: "#153591"],
					[value: 10, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 20, color: "#44b621"],
					[value: 25, color: "#f1d801"],
					[value: 30, color: "#d04e00"],
					[value: 35, color: "#bc2323"]
				],
                icon:"st.Weather.weather2"
			)
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }

		main(["temp1", "temp2"])
		details(["temp1", "temp2", "refresh", ])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	log.debug "Parse ${description}"
    def pair = description.split(":")
    createEvent(name: pair[0].trim(), value: pair[1].trim())
    /*
	def name = parseName(description)
	def value = parseValue(description)
	def unit = getTemperatureScale()
	def result = createEvent(name: name, value: value, unit: unit)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
    */
}

private String parseName(String description) {
	log.debug "parseName: ${description}"
	if (description?.startsWith("temp1:")) {
		return "temp1"
	} else if (description?.startsWith("temp2:")) {
		return "temp2"
	}

	null
}

private String parseValue(String description) {
	log.debug "parseValue: ${description}"
	if (description?.startsWith("temp")) {
		return 19//zigbee.parseHATemperatureValue(description, "temperature: ", getTemperatureScale())
	}
	null
}

def installed() {
	initialize()
    log.debug "Installed"
}

def updated() {
	initialize()
    log.debug "Updated"
}

def initialize() {
	getTemp(temp1Var)
	getTemp(temp1Var)
    log.debug "Initialized"
}

private getTemp(String tempName) {
	log.debug 'Requesting temp for: '+tempName
    def closure = { response ->
        log.debug "Temperature request was successful, $response.data"
        def rawTemp = response.data.result
        def parsedTemp = Math.round(rawTemp * 10) / 10
        sendEvent(name: tempName, value: parsedTemp)
    }
    def requestUrl = "https://api.particle.io/v1/devices/${deviceId}/"+tempName+"?access_token=${token}"
    log.debug "Request URL: " + requestUrl
    httpGet(requestUrl, closure)
}

def poll() {
    log.debug "Executing 'poll'"
	getTemp(temp1Var)
	getTemp(temp2Var)
}

def refresh() {
    log.debug "Executing 'refresh'"
	getTemp(temp1Var)
	getTemp(temp2Var)
}