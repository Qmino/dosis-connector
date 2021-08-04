package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value= OndernemerAgentTO.class, name = "OndernemerAgent"),
        @JsonSubTypes.Type(value= BurgerAgentTO.class, name="BurgerAgent")
})
public abstract class AgentTO {
}
