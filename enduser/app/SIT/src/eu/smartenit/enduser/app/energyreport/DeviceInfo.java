package eu.smartenit.enduser.app.energyreport;

public class DeviceInfo {
	private String identifier = "000000011111";
	private String name = "Tablet 1";
	private String type = "smartphone";
	private String model = "Nexus 7";
	private boolean batterypowered = true;
	private String batterystate = "discharging";
	private int cpucount = 2;
	private int cpumaxfreq = 1000;
	private NetworkInterfaces[] interfaces;

	public DeviceInfo(String identifier) {
		this.identifier = identifier;
		this.interfaces = new NetworkInterfaces[2];
		this.interfaces[0] = new NetworkInterfaces();

		this.interfaces[0].activated = true;
		this.interfaces[0].ip = "1.1.1.1";
		this.interfaces[0].mac = "ee:ee:ee:ee";
		this.interfaces[0].name = "CELL";
		this.interfaces[0].activated = true;

		this.interfaces[1] = new NetworkInterfaces();
		this.interfaces[1].ip = "1.1.1.1";
		this.interfaces[1].mac = "ff:ff:ff:ff";
		this.interfaces[1].name = "WLAN";
		this.interfaces[1].activated = true;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isBatterypowered() {
		return batterypowered;
	}

	public void setBatterypowered(boolean batterypowered) {
		this.batterypowered = batterypowered;
	}

	public String getBatterystate() {
		return batterystate;
	}

	public void setBatterystate(String batterystate) {
		this.batterystate = batterystate;
	}

	public int getCpucount() {
		return cpucount;
	}

	public void setCpucount(int cpucount) {
		this.cpucount = cpucount;
	}

	public int getCpumaxfreq() {
		return cpumaxfreq;
	}

	public void setCpumaxfreq(int cpumaxfreq) {
		this.cpumaxfreq = cpumaxfreq;
	}

	public NetworkInterfaces[] getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(NetworkInterfaces[] interfaces) {
		this.interfaces = interfaces;
	}

	class NetworkInterfaces {

		private String ip = "127.0.0.1";
		private String mac = "AA:BB:CC:DD:EE:FF";
		private String type = "WLAN";
		private boolean activated = true;
		private String name = "eth0";
		
		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isActivated() {
			return activated;
		}

		public void setActivated(boolean activated) {
			this.activated = activated;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

//<<<<<<< HEAD
//		private String name;
//		private String ip = "127.0.0.1";
//		private String mac = "AA:BB:CC:DD:EE:FF";
//		private String type = "WLAN";
//		private boolean activated = true;
//=======
//>>>>>>> ca61e33043351b64bf85a22934358473d925d192
	}
}
