package com.smt.beaconsmagnum.activities;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.connection.BeaconConnection;
import com.smt.beaconsmagnum.R;
import com.smt.beaconsmagnum.utils.BeaconsApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BeaconDataActivity extends Activity {
	
	  private Beacon beacon;
	  private BeaconConnection connection;

	  private TextView statusView;
	  private TextView beaconDetailsView;
//	  private EditText minorEditView;
	  private View afterConnectedView;
	  
	  private Button goToPromoBtn;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon_data);
		
	    statusView = (TextView) findViewById(R.id.status);
	    beaconDetailsView = (TextView) findViewById(R.id.beacon_details);
	    afterConnectedView = findViewById(R.id.after_connected);
//	    minorEditView = (EditText) findViewById(R.id.minor);

	    beacon = getIntent().getParcelableExtra("beacon");
	    connection = new BeaconConnection(this, beacon, createConnectionCallback());
//	    findViewById(R.id.update).setOnClickListener(createUpdateButtonListener());
	    
	    goToPromoBtn = (Button) findViewById(R.id.promoBtn);
	    
	  }

	  @Override
	  protected void onResume() {
	    super.onResume();
	    if (!connection.isConnected()) {
	      statusView.setText("Status: Connecting...");
	      connection.authenticate();
	    }
	  }

	  @Override
	  protected void onDestroy() {
	    connection.close();
	    super.onDestroy();
	  }

	  @Override
		public boolean onCreateOptionsMenu(Menu menu) {

			// Inflate the menu; this adds items to the action bar if it is present.
//			getMenuInflater().inflate(R.menu.beacon_data, menu);
			return true;
		}
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	      finish();
	      return true;
	    }
	    
	    // Handle action bar item clicks here. The action bar will
 		// automatically handle clicks on the Home/Up button, so long
 		// as you specify a parent activity in AndroidManifest.xml.
// 		int id = item.getItemId();
// 		if (id == R.id.action_settings) {
// 			return true;
// 		}
	    return super.onOptionsItemSelected(item);
	    	
	  }

	  /**
	   * Returns click listener on update minor button.
	   * Triggers update minor value on the beacon.
	   */
//	  private View.OnClickListener createUpdateButtonListener() {
//	    return new View.OnClickListener() {
//	      @Override public void onClick(View v) {
//	        int minor = parseMinorFromEditView();
//	        if (minor == -1) {
//	          showToast("Minor must be a number");
//	        } else {
//	          updateMinor(minor);
//	        }
//	      }
//	    };
//	  }

	  /**
	   * @return Parsed integer from edit text view or -1 if cannot be parsed.
	   */
//	  private int parseMinorFromEditView() {
//	    try {
//	      return Integer.parseInt(String.valueOf(minorEditView.getText()));
//	    } catch (NumberFormatException e) {
//	      return -1;
//	    }
//	  }

//	  private void updateMinor(int minor) {
//	    // Minor value will be normalized if it is not in the range.
//	    // Minor should be 16-bit unsigned integer.
//	    connection.writeMinor(minor, new BeaconConnection.WriteCallback() {
//	      @Override public void onSuccess() {
//	        runOnUiThread(new Runnable() {
//	          @Override public void run() {
//	            showToast("Minor value updated");
//	          }
//	        });
//	      }
//
//	      @Override public void onError() {
//	        runOnUiThread(new Runnable() {
//	          @Override public void run() {
//	            showToast("Minor not updated");
//	          }
//	        });
//	      }
//	    });
//	  }

	  private BeaconConnection.ConnectionCallback createConnectionCallback() {
	    return new BeaconConnection.ConnectionCallback() {
	      @Override public void onAuthenticated(final BeaconConnection.BeaconCharacteristics beaconChars) {
	        runOnUiThread(new Runnable() {
	          @Override public void run() {
	            statusView.setText("Status: Connected to beacon");
	            StringBuilder sb = new StringBuilder()
	            	.append("MAC: ").append(beacon.getMacAddress()).append("\n")
	                .append("Major: ").append(beacon.getMajor()).append("\n")
	                .append("Minor: ").append(beacon.getMinor()).append("\n")
	                .append("Advertising interval: ").append(beaconChars.getAdvertisingIntervalMillis()).append("ms\n")
	                .append("Broadcasting power: ").append(beaconChars.getBroadcastingPower()).append(" dBm\n")
	                .append("Battery: ").append(beaconChars.getBatteryPercent()).append(" %\n\n");
	            
	            if (beacon.getMinor() == 64444){
	            	
	            	sb.append("Hace un d�a perfecto para tomarse un Magnum Frac");
					
	            } else if (beacon.getMinor() == 36328){
	            	
	            	sb.append("Hoy comprando tu Magnum Sandwich, te regalamos otro para que disfrutes el doble!");
		   	  		
				} else if (beacon.getMinor() == 31394) {
					
					sb.append("S�lo por estar aqu� te regalamos un sabroso Magnum Strawberry & White");
		   	  		
				}
	            beaconDetailsView.setText(sb.toString());
//	            minorEditView.setText(String.valueOf(beacon.getMinor()));
	            afterConnectedView.setVisibility(View.VISIBLE);
	            
	            goToPromoBtn.setOnClickListener(new OnClickListener() {
	    			
	    			@Override
	    			public void onClick(View v) {
	    				String code = String.valueOf(beacon.getMinor());
	    				Intent targetIntent;
	    				if (code.equals("64444")){
	    					
	    					targetIntent = new Intent(BeaconDataActivity.this, WebViewActivity.class);
	    					targetIntent.putExtra("web", "http://www.frigo.es/Brand/Magnum.aspx");
	    					startActivity(targetIntent);
	    		       	  	
	    				} else if (code.equals("36328")){
	    					
	    					targetIntent = new Intent(BeaconDataActivity.this, ImageActivity.class);
	    					targetIntent.putExtra("image", "magnum_sandwich");
	    					startActivity(targetIntent);
	    		       	  	
	    				} else if (code.equals("31394")) {
	    					
	    					targetIntent = new Intent(BeaconDataActivity.this, ImageActivity.class);
	    					targetIntent.putExtra("image", "fresa1");
	    					startActivity(targetIntent);
	    		   	  		
	    				}
	    				
	    			}
	    		});;
	          }
	        });
	      }

	      @Override public void onAuthenticationError() {
	        runOnUiThread(new Runnable() {
	          @Override public void run() {
	            statusView.setText("Status: Cannot connect to beacon. Authentication problems.");
	          }
	        });
	      }

	      @Override public void onDisconnected() {
	        runOnUiThread(new Runnable() {
	          @Override public void run() {
	            statusView.setText("Status: Disconnected from beacon");
	          }
	        });
	      }
	    };
	  }

	  private void showToast(String text) {
	    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	  }

}
