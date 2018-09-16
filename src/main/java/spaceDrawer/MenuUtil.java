package spaceDrawer;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuUtil {
	
	public enum BackGroundColor{
		Black, White, Checkerd
	}
	
	public enum OnOrOff{
		On, Off
	}
	
	public interface MenuCallback{
	
		void saveImageFile();
	}
	
	public static MenuCallback menuCallback;

	public static MenuBar generateMenu(){
	
		MenuBar menuBar = new MenuBar();
		
		String[] menuNames = {"File","Setting"};
		Menu[] menus = generateMenuArray(menuNames);
		menuBar.getMenus().addAll(menus);
		
		setFileMenu(menus[0]);
		setSettingMenu(menus[1]);
	
		return menuBar;
	}
	
	private static void setFileMenu(Menu menu){
		
		String[] menuItemNames = {"Save Image File"};
		MenuItem[] menuItems = generateMenuItemArray(menuItemNames);
		menu.getItems().addAll(menuItems);
		
		menuItems[0].setOnAction(event->{menuCallback.saveImageFile();});
	}
	
	private static void setSettingMenu(Menu menu){
		
		String[] menuNames = {"BackGroundColor", "FrameGrid"};
		Menu[] menus = generateMenuArray(menuNames);
		menu.getItems().addAll(menus);
		
		setBackGroundColorMenu(menus[0]);
	}
	
	private static void setBackGroundColorMenu(Menu menu){
		
		MenuItem[] menuItems = generateMenuItemArray(BackGroundColor.values());
		menu.getItems().addAll(menuItems);
		
		for(BackGroundColor e: BackGroundColor.values()){
			
			menuItems[e.ordinal()].setOnAction(event -> {});
		}
	}
	
	private static Menu[] generateMenuArray(String[] menuNames){
		
		Menu[] menus = new Menu[menuNames.length];
		for(int i=0; i<menus.length; i++){
			menus[i] = new Menu(menuNames[i]);
		}
		return menus;
	}
	
	private static MenuItem[] generateMenuItemArray(Object[] menuItemEnums){
		
		MenuItem[] menuItems = new MenuItem[menuItemEnums.length];
		for(int i=0; i<menuItems.length; i++){
			menuItems[i] = new MenuItem(menuItemEnums[i].toString());
		}
		return menuItems;
	}
}
