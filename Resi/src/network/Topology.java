package network;

import config.Constant;
import graph.Coordination;
import graph.Graph;
import network.host.Host;

import routing.RoutingAlgorithm;
import simulator.Simulator;

import java.util.*;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Topology {
    private Graph graph;
    private List<Host> hosts;
    private List<Switch> switches;
    private Map<Integer, Host> hostById;
    private Map<Integer, Switch> switchById;

    //ThanhNT 14/10 new property
    public Map<Integer, String> cordOfNodes;
    //Endof ThanhNT 14/10 new property

    public Topology(Graph graph, RoutingAlgorithm routingAlgorithm) {
        this.graph = graph;
        // construct hosts, switches and links and routing algorithm
        hosts = new ArrayList<>();
        switches = new ArrayList<>();
        hostById = new HashMap<>();
        switchById = new HashMap<>();

        //ThanhNT 14/10 add new statements to init property
        cordOfNodes = new HashMap<>();
        //Endof ThanhNT 14/10 add new statements to init property

        for (int hid : graph.hosts()) {
            Host host = new Host(hid);
            hosts.add(host);
            hostById.put(hid, host);

            //ThanhNT 14/10 add new statements to add new ID of HOST
            cordOfNodes.put(hid, "");
            //Endof ThanhNT 14/10 add new statements to add new ID of HOST
        }

        for (int sid : graph.switches()) {
            Switch sw = new Switch(sid
            		//, routingAlgorithm
            		);
            switches.add(sw);
            switchById.put(sid, sw);

            //ThanhNT 14/10 add new statements to add new ID of switch
            cordOfNodes.put(sid, "");
            //Endof ThanhNT 14/10 add new statements to add new ID of switch
        }


        // link from switch to switch
        Coordination C = new Coordination(graph);
        for (Switch sw : switches) {
            int swid = sw.id;
            for (int nsid : graph.adj(swid)) {
                if (graph.isSwitchVertex(nsid)) {
                    Switch other = switchById.get(nsid);
                    // => ThanhNT set comment to THE following line
                    //if (!other.ports.containsKey(swid)) 
                    {
                        // create new link
                        double x =  C.distanceBetween(sw.id, other.id);
                        System.out.println("Chieu dai leng = " + x + " from: " + sw.id + " to: " + other.id);
                        //double x = 5;
                        Link link = new Link(sw, other, x);
                        // => ThanhNT set comment to 2 following lines
                        //other.ports.put(swid, link.getPort(other));
                        //sw.ports.put(nsid, link.getPort(sw));
                        //ThanhNT 14/10 add new statements to insert coord of switch
                        cordOfNodes.put(sw.id, C.getCoordOfSwitch(sw.id));
                        cordOfNodes.put(other.id, C.getCoordOfSwitch(other.id));
                        //Endof ThanhNT 14/10 add new statements to insert coord of switch
                    }
                }
            }
        }

        // link from switch to host
        for (Host host : hosts) {
            // get switch
            int nsid = graph.adj(host.id)
                    .get(0);
            Switch csw = switchById.get(nsid);

            // create new link
            Link link = new Link(host, csw, Constant.HOST_TO_SWITCH_LENGTH);

            //// add link to both => ThanhNT set comment to 2 following lines
            //host.portToSwitch = link.getPort(host);
            //csw.ports.put(host.id, link.getPort(csw));
            //ThanhNT 14/10 add new statements to insert coord of HOST
            cordOfNodes.put(host.id, C.getCoordOfHost(csw.id, Constant.HOST_TO_SWITCH_LENGTH));
            //Endof ThanhNT 14/10 add new statements to insert coord of HOST
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public List<Switch> getSwitches() {
        return switches;
    }

    public Host getHostById(int id) {
        return hostById.get(id);
    }

    public void clear() {
        for (Host host : hosts) {
            host.clear();
        }

        for (Switch sw: switches) {
            sw.clear();
        }
    }
    
    public void setSimulator(Simulator sim)
    {
    	for (Host host : hosts) {
            host.clear();
            host.physicalLayer.sim = sim;
        }

        for (Switch sw: switches) {
            sw.clear();
        }
        
        sim.network = this;
    }

    public boolean checkDeadlock(){
    	return false;
    }
}
