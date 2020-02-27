package ch.zuehlke.bench.transport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Journey")
@XmlAccessorType(XmlAccessType.FIELD)
public class Journey {

    @XmlAttribute
    private String fpTime;
    @XmlAttribute
    private String fpDate;
    @XmlAttribute
    private String delay; //"+ 18"
    @XmlAttribute
    private String e_delay; //"19"
    @XmlAttribute
    private String newpl;
    @XmlAttribute
    private String platform;
    @XmlAttribute
    private String targetLoc;
    @XmlAttribute
    private String prod; //IR 15#IR
    @XmlAttribute
    private String dir; //"Gen&#232;ve-A&#233;roport
    @XmlAttribute
    private String capacity; //"1|1"
    @XmlAttribute
    private String is_reachable; //"0"
    @XmlElement
    private HIMMessage himMessage;

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getE_delay() {
        return e_delay;
    }

    public void setE_delay(String e_delay) {
        this.e_delay = e_delay;
    }

    public String getFpDate() {
        return fpDate;
    }

    public void setFpDate(String fpDate) {
        this.fpDate = fpDate;
    }

    public String getFpTime() {
        return fpTime;
    }

    public void setFpTime(String fpTime) {
        this.fpTime = fpTime;
    }

    public HIMMessage getHimMessage() {
        return himMessage;
    }

    public void setHimMessage(HIMMessage himMessage) {
        this.himMessage = himMessage;
    }

    public String getIs_reachable() {
        return is_reachable;
    }

    public void setIs_reachable(String is_reachable) {
        this.is_reachable = is_reachable;
    }

    public String getNewpl() {
        return newpl;
    }

    public void setNewpl(String newpl) {
        this.newpl = newpl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getTargetLoc() {
        return targetLoc;
    }

    public void setTargetLoc(String targetLoc) {
        this.targetLoc = targetLoc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("next depature ").append(fpTime);
        if (!"-".equals(delay)) {
            if (e_delay != null && !e_delay.isEmpty()) {
                sb.append(" +").append(e_delay);
            } else {
                sb.append(" ").append(delay);
            }
        }
        if (newpl != null || platform != null) {
            sb.append(" at ").append(newpl != null ? newpl + "!" : platform);
        }
        if (!("0|0".equals(capacity))) {
            sb.append(" (").append(capacity).append(")");
        }
        if (himMessage != null) {
            sb.append("\n").append(himMessage.toString());
        }
        return sb.toString();
    }

    public static class HIMMessage {
        private String header;
        private String lead;
        private String display;

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getLead() {
            return lead;
        }

        public void setLead(String lead) {
            this.lead = lead;
        }

        @Override
        public String toString() {
            return "HIMMessage{" +
                    "header='" + header + '\'' +
                    ", lead='" + lead + '\'' +
                    ", display='" + display + '\'' +
                    '}';
        }
    }
}
