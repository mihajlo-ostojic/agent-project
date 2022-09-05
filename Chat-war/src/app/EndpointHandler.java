package app;

public interface EndpointHandler<T> {
	public void handle(T endpointProxy);
}
