FOR %%L IN (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z) DO (
	curl "http://cloud.tfl.gov.uk/TrackerNet/PredictionSummary/%%L" > PredictionSummary-%%L.xml
)