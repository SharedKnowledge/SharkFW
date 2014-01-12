/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

/**
 *
 * @author Romy Gerlach
 */
public class TopicAdd extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);

    public TopicAdd(FormHandler handler) {
        super("Topic add");
        this.handler = handler;
        this.addCommand(commandExit);
        this.addCommand(commandMenu);
        this.setCommandListener(this);

    }

    public int append(String str) {
        return super.append(str);
    }

    public void commandAction(Command c, Displayable d) {
//        switch (c.getCommandType()) {
//            case Command.BACK:
//                handler.switchForm(MEISphere.FORM_MENU);
//                break;
//            case Command.EXIT:
//                handler.switchForm(MEISphere.EXIT);
//                break;
//
//        }
    }
}


