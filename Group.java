package vanunu.deeznuts;

/**
 * Created by Nevo Vanunu on 31/12/2017.
 */

public class Group {

    String name,code,id;
public Group()
{

}
    public Group(String name, String code, String id) {
        this.name=name;
        this.code=code;
        this.id=id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
