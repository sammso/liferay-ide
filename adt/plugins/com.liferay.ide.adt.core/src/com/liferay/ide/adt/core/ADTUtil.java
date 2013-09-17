
package com.liferay.ide.adt.core;

import com.android.sdklib.AndroidVersion;
import com.android.sdklib.IAndroidTarget;


/**
 * @author Gregory Amerson
 */
public class ADTUtil
{

    public static int extractSdkLevel( String content )
    {
        return Integer.parseInt( content.substring( content.indexOf( "API " ) + 4, content.indexOf( ":" ) ) );
    }

    public static String getTargetLabel( IAndroidTarget target )
    {
        if( target.isPlatform() )
        {
            AndroidVersion version = target.getVersion();
            String codename = target.getProperty( "Platform.CodeName" );
            String release = target.getProperty( "ro.build.version.release" );
            if( codename != null )
            {
                return String.format(
                    "API %1$d: Android %2$s (%3$s)", new Object[] { Integer.valueOf( version.getApiLevel() ), release,
                        codename } );
            }
            return String.format( "API %1$d: Android %2$s", new Object[] { Integer.valueOf( version.getApiLevel() ),
                release } );
        }

        return String.format( "%1$s (API %2$s)", new Object[] { target.getFullName(),
            target.getVersion().getApiString() } );
    }

}
