package aqua.blatt1.broker;

import java.net.InetSocketAddress;
import aqua.blatt1.common.Direction;
import aqua.blatt1.common.msgtypes.DeregisterRequest;
import aqua.blatt1.common.msgtypes.HandoffRequest;
import aqua.blatt1.common.msgtypes.RegisterRequest;
import aqua.blatt1.common.msgtypes.RegisterResponse;
import messaging.Endpoint;
import messaging.Message;

public class Broker {

	static int i = 1;

	static Endpoint endpoint = new Endpoint(4711);

	static ClientCollection<InetSocketAddress> list = new ClientCollection<>();

	public static void broker() {

		while (true) {

			Message message = endpoint.blockingReceive();

			if (message.getPayload() instanceof RegisterRequest) {

				register(message.getSender());

			} else if (message.getPayload() instanceof HandoffRequest) {

				handoff(message.getSender(), (HandoffRequest) message.getPayload());

			} else if (message.getPayload() instanceof DeregisterRequest) {

				deregister((DeregisterRequest) message.getPayload());

			}

		}
	}

	public static void register(InetSocketAddress sender) {

		String id = "Aquarium " + i;
		i++;

		list.add(id, sender);

		endpoint.send(sender, new RegisterResponse(id));

	}

	public static void handoff(InetSocketAddress sender, HandoffRequest handoff) {
		
		InetSocketAddress temp;

		int index = list.indexOf(sender);
		if (handoff.getFish().getDirection().equals(Direction.LEFT)) {
			temp = list.getLeftNeighorOf(index);
		} else {
			temp = list.getRightNeighorOf(index);
		}
		endpoint.send(temp, handoff);

	}

	public static void deregister(DeregisterRequest deregisterRequest) {

		int index = list.indexOf(deregisterRequest.getId());
		
		if (index != -1) {
			list.remove(index);
		}

	}

	public static void main(String args[]) {
		broker();
	}
}
