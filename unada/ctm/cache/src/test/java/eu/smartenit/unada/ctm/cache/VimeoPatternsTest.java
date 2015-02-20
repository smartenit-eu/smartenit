/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.unada.ctm.cache;

import java.util.regex.Pattern;

import eu.smartenit.unada.ctm.cache.util.VimeoPatterns;
import org.junit.Assert;
import org.junit.Test;


public class VimeoPatternsTest {
	
	String vimeoUrl1 = "https://vimeo.com/daa/ddad/98658153";
	String vimeoUrl2 = "https://vimeo.com/99610151";
	String vimeoUrl3 = "http://vimeo.com/m/99610151";
	String vimeoUrl4 = "https://vimeo.com//99610151";
	
	String vimeoRequest1 = "https://avvimeo-a.akamaihd.net/03110/750/73389900.mp4";
	String vimeoRequest2 = "http://av.vimeo.com/52647/777/46233263.mp4";
	String vimeoRequest3 = "http://pdl.vimeocdn.com/52647/777/46233263.mp4";
    String vimeoRequest4 = "http://a.vimeo.com/video/5264724242424/46233263.mp4";
    String vimeoRequest5 = "https://whatever.com/video/5264724242424/46233263.mp4";
	
	String vimeoStreamRequest1 = "https://avvimeo-a.akamaihd.net/03110/750/73389900.mp4?token2=1424271022_61c215decf0b3cf12c582672c65abcf0&aksessionid=6d9b63f2b7fd8b88&ns=4";
	String vimeoStreamRequest2 = "http://av.vimeo.com/52647/777/46233263.mp4?fasfafsafsafasfasfa";
	String vimeoStreamRequest3 = "https://a.vimeo.com/52647/777/46233263.mp4?fasfsafasfase2qw3dsa41rwwf2r2";
    String vimeoStreamRequest4 = "http://a.vimeo.com/video/52647/777/46233263.mp4?fasfsafasfase2qw3dsa41rwwf2r2";
    String vimeoStreamRequest5 = "http://whatever.com/video/52647/777/46233263.mp4?fasfsafasfase2qw3dsa41rwwf2r2";
		
	@Test
	public void testPatterns() throws Exception {
		
		Pattern pattern1 = Pattern.compile(VimeoPatterns.vimeoURL);
		Pattern pattern2 = Pattern.compile(VimeoPatterns.vimeoRequest);
		Pattern pattern3 = Pattern.compile(VimeoPatterns.vimeoFolders);
		
		
		Assert.assertTrue(pattern1.matcher(vimeoUrl1).matches());
		Assert.assertTrue(pattern1.matcher(vimeoUrl2).matches());
		Assert.assertTrue(pattern1.matcher(vimeoUrl3).matches());
		Assert.assertFalse(pattern1.matcher(vimeoUrl4).matches()); // should not match
		
		Assert.assertTrue(pattern2.matcher(vimeoRequest1).matches());
		Assert.assertTrue(pattern2.matcher(vimeoRequest2).matches());
		Assert.assertTrue(pattern2.matcher(vimeoRequest3).matches());
        Assert.assertTrue(pattern2.matcher(vimeoRequest4).matches());
        Assert.assertFalse(pattern2.matcher(vimeoRequest5).matches()); //should not match

        Assert.assertTrue(pattern3.matcher(vimeoStreamRequest1).matches());
		Assert.assertTrue(pattern3.matcher(vimeoStreamRequest2).matches());
		Assert.assertTrue(pattern3.matcher(vimeoStreamRequest3).matches());
        Assert.assertTrue(pattern3.matcher(vimeoStreamRequest4).matches());
        Assert.assertFalse(pattern3.matcher(vimeoStreamRequest5).matches()); //should not match

        Assert.assertFalse(pattern1.matcher(vimeoRequest1).matches());
		Assert.assertFalse(pattern2.matcher(vimeoUrl1).matches());
		Assert.assertFalse(pattern2.matcher(vimeoUrl2).matches());		
	}

}
