package com.tourist;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class aroundme extends ListActivity{
	
	private final static String NAMESPACE = "http://tempuri.org/";
	//private final static String URL = "http://192.168.1.2/TouristWS/MobileClient.asmx";
	private final static String URL = "http://94.230.186.46/TouristWS/MobileClient.asmx";
	private String serverURL = "94.230.186.88";
	//private String serverURL = "www.google.com";
	private boolean bLocalhostIP;
	
	private int iRadius = 800;
	
	
	private int[] arrSettings = new int[2];				//niz Settings podataka
	private ArrayList<Locality> arrLocalities = null;	//niz objekata Locality
	private LocalityAdapter localityAdapter;			//adapter za popunu liste
	private TouristDbAdapter mDbHelper;					//adapter za manipulaciju sa bazom 
	
	Toast tost;											//notifikacioni objekat Toast
	LocationManager locationManager;					//objekat menadzer lokacije
	LocationListener locationListener;					//objekat listener menadzera lokacije
	ListView lv;										//objekat glavna lista na ovom Activity
	Button btnRange;									//dugme za razdaljinu
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aroundme);
        
        btnRange = (Button) findViewById(R.id.btnRange);
        registerForContextMenu(btnRange);
        btnRange.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnRange.setText(""+iRadius+" m");
			}
		});
        
        //init();
        bLocalhostIP = true;
        
        if(bLocalhostIP){
	        WSgetSettings();
	 
	        // Acquire a reference to the system Location Manager
	        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	        
	        // Define a listener that responds to location updates
	        locationListener = new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {}
				
				@Override
				public void onProviderEnabled(String provider) {}
				
				@Override
				public void onProviderDisabled(String provider) {}
				
				// Called when a new location is found by the network location provider.
	        	// podaci o koordinatama se moraju slati preko TELNET-a dok se koristi emulator
	        	// telnet localhost 5554
				@Override
				public void onLocationChanged(Location location) {
					//izvesti(location.getLatitude(), location.getLongitude());
					SelectLocality_Radius_NoCategory(location.getLatitude(), location.getLongitude(), iRadius);
					
					//METODA KOJA PUNI LISTU
	            	fillData();
					
				}
			};
			
			// Register the listener with the Location Manager to receive location updates
	        // Preporuka je da interval novog ocitavanja GPS-a ne bude ispod 60000 ms
	        // 10m je minimalni radijus promene
	        
	        int minTime = arrSettings[0];
	        int minDistance = arrSettings[1];
	        
	        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
        }else{
        	String test = "Server nije na raspolaganju";
        	tost = Toast.makeText(this, test, Toast.LENGTH_SHORT);
        	tost.show();
        }
		
    }

    @Override
	public void onStop()
	{
		super.onStop();
		tost.cancel();
		if(bLocalhostIP){
			locationManager.removeUpdates(locationListener);
		}
		btnRange.setOnClickListener(null);
	}
    
    @Override
    public void onDestroy()
    {
    	localityAdapter.imageLoader.clearCache();
    	localityAdapter.notifyDataSetChanged();
    	localityAdapter.imageLoader.stopThread();
        lv.setAdapter(null);
        super.onDestroy();
    }
    
    /* -------- CONTEXT (FLOAT) MENU -------- */
    @Override 
	public void onCreateContextMenu(ContextMenu cMenu, View v, ContextMenuInfo menuInfo)
	{

		super.onCreateContextMenu(cMenu, v, menuInfo);
		MenuInflater cInflater = getMenuInflater();
		cInflater.inflate(R.menu.aroundme_checkable_radius, cMenu);
		
	}
    
    @Override
    public boolean onContextItemSelected (MenuItem item) {
    	switch (item.getItemId())
    	{
    	case R.id.meters70:
    		btnRange.setText("70 m");
    		iRadius = 70;
    		return true;
    	case R.id.meters100:
    		btnRange.setText("100 m");
    		iRadius = 100;
    		return true;
    	case R.id.meters200:
    		btnRange.setText("200 m");
    		iRadius = 200;
    		return true;
    	case R.id.meters500:
    		btnRange.setText("500 m");
    		iRadius = 500;
    		return true;
    	case R.id.meters800:
    		btnRange.setText("800 m");
    		iRadius = 800;
    		return true;
    	case R.id.meters1000:
    		btnRange.setText("1000 m");
    		iRadius = 1000;
    		return true;
    	case R.id.meters1500:
    		btnRange.setText("1500 m");
    		iRadius = 1500;
    		return true;
    	case R.id.meters2000:
    		btnRange.setText("2000 m");
    		iRadius = 2000;
    		return true;
    	case R.id.meters3000:
    		btnRange.setText("3000 m");
    		iRadius = 3000;
    		return true;
    	case R.id.meters5000:
    		btnRange.setText("5000 m");
    		iRadius = 5000;
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    private void init()
    {
    	try{
	    	Runtime runtime = Runtime.getRuntime();
	    	Process proc = runtime.exec("ping "+serverURL+" -n 1"); // other servers, for example
	    	proc.waitFor();
	    	int exit = proc.exitValue();
	    	if (exit > 0) { // normal exit
	    	    /* get output content of executing the ping command and parse it
	    	     * to decide if the server is reachable
	    	     */
	    		bLocalhostIP = true;
	    	} else { // abnormal exit, so decide that the server is not reachable
	    		bLocalhostIP = false;
	    	}
    	}
    	catch(Exception ex)
    	{
    		
    	}
    	
    	/*
    	try
        {
    		int timeout = 2000;
    		InetAddress ia = InetAddress.getByName(serverURL);
        	bLocalhostIP = ia.isReachable(timeout);
        }
        catch(Exception ex){}
        */
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
        	setResult(20); //any int number is fine
        	//LocationList.this.finish();
            //return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
    /* ---------- methodes ------------------------------------------------------------ */
    private void izvesti(double dLat, double dLng)
    {
    	String test = "NOVA POZICIJA: " + dLat + " | " + dLng;
    	tost = Toast.makeText(this, test, Toast.LENGTH_SHORT);
    	tost.show();
    }
    
    private void fillData()
    {
    	try
    	{
    		
    		localityAdapter = new LocalityAdapter(this, R.layout.aroundme_list_row2, this.arrLocalities, this);
	        setListAdapter(localityAdapter);
	        lv = getListView();
	    	//lv.setTextFilterEnabled(true);
    	}
		catch(Exception e)
		{
			String test = "Nema lokaliteta.";
    		tost = Toast.makeText(this, test, Toast.LENGTH_SHORT);
    		tost.show();
    	}
    	
    }
    
    private void onClick_Radius()
    {
    	
    }
    
    
    /* ---------- WEB SERVICE --------------------------------------------------------- */
    private void WSgetSettings()
    {
    	String SOAP_ACTION = "http://tempuri.org/SelectSettingsAll";
    	String METHOD_NAME = "SelectSettingsAll";
    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //request.addProperty("strIme", "Bane");
        //request.addProperty("iBrojPozdrava", 2);
        
    	SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	soapEnvelope.dotNet = true;
        soapEnvelope.setOutputSoapObject(request);
        AndroidHttpTransport aht = new AndroidHttpTransport(URL);
        
        try
        {
        	aht.call(SOAP_ACTION, soapEnvelope);
        	SoapPrimitive stringResult = (SoapPrimitive) soapEnvelope.getResponse();
        	
        	Parser par = new Parser();

        	arrSettings[0] = Integer.parseInt(par.parseData(stringResult.toString(), 0, 1));/// SET_GpsMinTime
        	arrSettings[1] = Integer.parseInt(par.parseData(stringResult.toString(), 0, 2));/// SET_GpsMinDistance
        	
        	String test = "Ucitan Settings: " + arrSettings[0] + " : " + arrSettings[1];
        	tost = Toast.makeText(this, test, Toast.LENGTH_SHORT);
        	tost.show();
        	
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    	
    }
    
    private void SelectLocality_Radius_NoCategory(double dLat, double dLng, int iRadius)
    {
    	String SOAP_ACTION = "http://tempuri.org/SelectLocality_Radius_NoCategory";
    	String METHOD_NAME = "SelectLocality_Radius_NoCategory";
    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("sLat", ""+dLat);
        request.addProperty("sLng", ""+dLng);
        request.addProperty("iRadius", iRadius); // sve double promenljive moram da pretvorim u string jer u protivnom ne moze da se serijalizuje
        
    	SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	soapEnvelope.dotNet = true;
        soapEnvelope.setOutputSoapObject(request);
        AndroidHttpTransport aht = new AndroidHttpTransport(URL);
        
        try
        {
        	aht.call(SOAP_ACTION, soapEnvelope);
        	SoapPrimitive stringResult = (SoapPrimitive) soapEnvelope.getResponse();
        	
        	
        	//resultRequestSOAP =  soapEnvelope.getResponse();
        	//String[] stringResult = (String[])  resultRequestSOAP;
        	
        	Parser par = new Parser();
        	int brojRedova = par.countRows(stringResult.toString());
        	
        	arrLocalities = new ArrayList<Locality>(); // ovim instanciranjem se ujedno resetuje lista
        	
        	for(int i=0; i<brojRedova; i++)
        	{
        		Locality tempLocality = new Locality();
        		
        		int iLOC_ID = Integer.parseInt(par.parseData(stringResult.toString(), i, 0));
        		tempLocality.setID(iLOC_ID);
        		
        		String sLOC_Title = par.parseData(stringResult.toString(), i, 1);
        		tempLocality.setTitle(sLOC_Title);
        		
        		String sLOC_Address = par.parseData(stringResult.toString(), i, 2);
        		tempLocality.setAddress(sLOC_Address);
        		
        		String sLat = par.parseData(stringResult.toString(), i, 3);
        		//String newSLat = sLat.replace(".", ",");
        		double dLOC_Lat = Double.parseDouble(sLat);
        		tempLocality.setLat(dLOC_Lat);
        		
        		String sLng = par.parseData(stringResult.toString(), i, 4);
        		//String newSLng = sLng.replace(".", ",");
        		double dLOC_Lng = Double.parseDouble(sLng);
        		tempLocality.setLng(dLOC_Lng);
        		
        		String sLOC_Description = par.parseData(stringResult.toString(), i, 5);
        		tempLocality.setDescription(sLOC_Description);
        		
        		String sLOC_PhotoIcon = par.parseData(stringResult.toString(), i, 6);
        		tempLocality.setPhotoIcon(sLOC_PhotoIcon);
        		
        		String sLOC_Phone = par.parseData(stringResult.toString(), i, 8);
        		tempLocality.setPhone(sLOC_Phone);
        		
        		String sLOC_Website = par.parseData(stringResult.toString(), i, 9);
        		tempLocality.setWebsite(sLOC_Website);
        		
        		//myLocalities.add(new Locality(iLOC_ID, sLOC_Title, sLOC_Address, sLOC_Phone, sLOC_Website, dLOC_Lat, dLOC_Lng, sLOC_Description, sLOC_PhotoIcon));
        		arrLocalities.add(tempLocality);
        	}
        	
        	String test = "Ucitano " + brojRedova + " lokaliteta";
        	tost = Toast.makeText(this, test, Toast.LENGTH_SHORT);
        	tost.show();
        	
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    	
    }
    
    /* ------------ END OF WEB SERVICE ----------------------*/
    
    
    private class LocalityAdapter extends ArrayAdapter<Locality>
    {
    	private ArrayList<Locality> items;
    	private final static String photoURL = "http://94.230.186.46/touristwebportal/images/photos/";
    	public ImageLoader imageLoader;
    	private Activity activity;
    	
    	public LocalityAdapter(Context context, int textViewResourceId, ArrayList<Locality> items, Activity activity) {
            super(context, textViewResourceId, items);
            this.items = items;
            imageLoader=new ImageLoader(activity.getApplicationContext());
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent)
    	{
    		View v = convertView;
    		if(v == null)
    		{
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			v = vi.inflate(R.layout.aroundme_list_row2, null);
    		}
    		
    		Locality l = items.get(position);
    		if(l != null)
    		{
    			TextView txtTitle = (TextView)v.findViewById(R.id.textView_LocTitle2);
    			TextView txtAddress = (TextView)v.findViewById(R.id.textView_LocAddress2);
    			ImageView ivImage = (ImageView)v.findViewById(R.id.imageView_LocPhotoIcon2);
    			if(txtTitle != null)
    			{
    				txtTitle.setText(l.getTitle());
    			}
    			if(txtAddress != null)
    			{
    				txtAddress.setText(l.getAddress());
    			}
    			if(ivImage != null)
    			{
    				ivImage.setTag(photoURL + l.getPhotoIcon());
    				imageLoader.DisplayImage(photoURL + l.getPhotoIcon(), activity, ivImage);
    			}
    		}
    		
    		return v;
    	}
    }
    
}
