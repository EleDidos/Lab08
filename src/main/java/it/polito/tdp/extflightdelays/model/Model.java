package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private Map<Integer, Airport> idMap; //ID dell'oggetto vertice   -   Vertici
	private ExtFlightDelaysDAO dao;
	
	public Model() {
		idMap = new HashMap<Integer,Airport>();
		dao = new ExtFlightDelaysDAO();
		
		// FUNZIONE DA MODIFICARE --> il dao mi riempie la MAPPA
		dao.loadAllAirports(idMap);
	}
	
	public void creaGrafo(int distanzaMedia) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		//Aggiungere tutti i vertici 
		//vertici = valori della mappa
		Graphs.addAllVertices(grafo, idMap.values());
		
		for(Rotta rotta : dao.getRotte(idMap, distanzaMedia)) {
			//controllo se esiste già un arco
			//se esiste, faccio una media tra peso vecchio e nuovo
			DefaultWeightedEdge edge = grafo.getEdge(rotta.getA1(), rotta.getA2());
			if(edge == null) {
				Graphs.addEdge(grafo, rotta.getA1(), rotta.getA2(), rotta.getPeso());
			} else {
				System.out.format("l'arco che parte da: %s e arriva a %s ESISTEVA GIA' \n", rotta.getA1().getAirportName(),rotta.getA2().getAirportName());
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + rotta.getPeso())/2;
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Rotta> getRotte(){
		//uso la classe Rotta per salvare gli archi del grafo con il relativo peso
		List<Rotta> rotte = new ArrayList<Rotta>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			rotte.add(new Rotta(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
		}
		return rotte;
	}
	
}
