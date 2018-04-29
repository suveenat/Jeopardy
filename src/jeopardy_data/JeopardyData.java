package jeopardy_data;

/**
 * Created by Suveena on 11/3/16.
 */
import java.io.ObjectInput;
import java.io.Serializable;

public interface JeopardyData extends Serializable {
    public static final long serialVersionUID = 1;

    Object object = new Object();

    public Object getData();

}
