package be.vlaio.dosis.connector.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value=OndernemerAgent.class, name = "ondernemeragent"),
        @JsonSubTypes.Type(value=BurgerAgent.class, name="burgeragent")
})
public abstract class Agent {
}
