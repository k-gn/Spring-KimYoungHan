package hello.proxy.pureproxy.decorator.code;

public abstract class Decorator implements Component {

	public final Component component;

	public Decorator(Component component) {
		this.component = component;
	}

	public abstract String operation();
}
