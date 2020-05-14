package ch.zuehlke.bench;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "telegram-hook",
                version = "1.0.0",
                contact = @Contact(
                        name = "Telegram Hook Example",
                        url = "https://github.com/h2mch/telegram-hook",
                        email = "github@h2m.ch"),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"))
)
public class TelegramHookApplication extends javax.ws.rs.core.Application {
}
