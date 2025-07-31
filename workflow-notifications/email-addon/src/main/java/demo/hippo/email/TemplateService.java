package demo.hippo.email;

public interface TemplateService {
    String getTemplateByName(String name);

    String getPropertyByName(String name, String propertyName);
}
