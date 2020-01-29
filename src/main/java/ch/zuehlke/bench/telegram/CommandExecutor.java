package ch.zuehlke.bench.telegram;

import java.util.Optional;

import ch.zuehlke.bench.transport.DelayService;
import ch.zuehlke.bench.weather.WeatherService;

public class CommandExecutor {

    public static final String PREFIX = "/";

    enum COMMAND {
        LUZ_BRN(DelayService.class, "getNextDeparture", "LUZ", "BRN"),
        BRN_LUZ(DelayService.class, "getNextDeparture", "BRN", "LUZ"),
        LUZ_BAS(DelayService.class, "getNextDeparture", "LUZ", "BAS"),
        BAS_LUZ(DelayService.class, "getNextDeparture", "BAS", "LUZ"),
        LUZ_ZRH(DelayService.class, "getNextDeparture", "LUZ", "ZRH"),
        ZRH_LUZ(DelayService.class, "getNextDeparture", "ZRH", "LUZ"),
        LUZ(WeatherService.class, "getCurrentWeather", "LUZ"),
        TIT(WeatherService.class, "getCurrentWeather", "TIT"),
        BER(WeatherService.class, "getCurrentWeather", "BER"),
        ENG(WeatherService.class, "getCurrentWeather", "ENG")
        ;


        private Class clazz;
        private String method;
        private String[] parameter;

        COMMAND(Class clazz, String method, String... parameter) {
            this.clazz = clazz;
            this.method = method;
            this.parameter = parameter;
        }

        public String[] getParameter() {
            return parameter;
        }
    }

    public static Optional<COMMAND> parseCommand(String command) {
        if (command == null || command.isEmpty() || !command.startsWith(PREFIX)) {
            return null;
        }

        String plainCmd = command.substring(1);

        for (COMMAND cmd : COMMAND.values()) {
            if (plainCmd.endsWith(cmd.name())) {
                return Optional.of(cmd);
            }
        }
        return Optional.empty();
    }
}
