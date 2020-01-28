package ch.zuehlke.bench.telegram;

public interface TelegramCommand {

    enum TOPIC {
        DELAY,
        WEATHER
    }

    enum COMMAND {
        LUZ_BRN(TOPIC.DELAY, "LUZ-BRN"),
        BRN_LUZ(TOPIC.DELAY, "BRN-LUZ"),
        LUZ_BAS(TOPIC.DELAY, "LUZ-BAS"),
        BAS_LUZ(TOPIC.DELAY, "BAS-LUZ"),
        LUZ_ZRH(TOPIC.DELAY, "LUZ-ZRH"),
        ZRH_LUZ(TOPIC.DELAY, "ZRH-LUZ"),
        LUZ(TOPIC.WEATHER, "LUZ"),
        TIT(TOPIC.WEATHER, "TIT"),
        BER(TOPIC.WEATHER, "BER"),
        ENG(TOPIC.WEATHER, "ENG"),
        UNKNOWN(null, null);


        private TOPIC topic;
        private String parameter;

        COMMAND(TOPIC topic, String parameter) {
            this.topic = topic;
            this.parameter = parameter;
        }

        public TOPIC getTopic() {
            return topic;
        }

        public String getParameter() {
            return parameter;
        }

        public static COMMAND parseCommand(String command) {
            if (command == null || command.isEmpty()) {
                return null;
            }

            for (COMMAND value : COMMAND.values()) {
                if (command.equals(value.getParameter())) {
                    return value;
                }
            }
            return COMMAND.UNKNOWN;
        }
    }
}
