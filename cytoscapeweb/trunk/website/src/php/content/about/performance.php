
<div class="left">
	<h1>Introduction</h1>
	<p>Cytoscape Web works best with small to medium sized networks, generally with up to a few hundred nodes and edges.
	   Larger networks can be visualized, but the user interaction can become sluggish around 2000 elements &#8212; 800 nodes and 1200 edges for example.</p>
	<p>Notice from the table bellow that the use of the force-directed layout is the major bottleneck in the initial rendering of a typical network.
	   Also overall performance is dependant upon the client website implementation and the end user configuration.</p>
	
	<h1>Rendering time</h1>
	<p>The following table shows the time in seconds to render different sized networks with different layout algorithms on supported operating systems and browsers.</p>
	

<table id="performance_table" cellspacing="0" cellpadding="0">
	<tr>
		<th class="empty">&nbsp;</th>
		<th class="empty">&nbsp;</th>
		<th class="hcol empty">&nbsp;</th>
		<th colspan="5">Network elements <small>nodes, edges</small></th>
	</tr>
	<tr>
		<th>&nbsp;</th>
		<th>&nbsp;</th>
		<th class="hcol">Layouts</th>
		<th class="hrow">150 <small>50, 100</small></th>
		<th class="hrow">300 <small>100, 200</small></th>
		<th class="hrow">600 <small>200, 400</small></th>
		<th class="hrow">1200 <small>400, 800</small></th>
		<th class="hrow">2400 <small>800, 1600</small></th>
	</tr>
	<tr>
		<th rowspan="8">Mac OS X<br /> 10.6.3</th>
		<th rowspan="4" class="rtitle"><small>Firefox<br /> 3.6.3</small></th>
		<td class="hcol">Circle</td>
		<td>0.84</td>
		<td>1.10</td>
		<td>1.36</td>
		<td>2.00</td>
		<td>3.43</td>
	</tr>
	<tr>
		<td class="hcol">Radial</td>
		<td>0.94</td>
		<td>1.11</td>
		<td>1.42</td>
		<td>2.09</td>
		<td>3.61</td>
	</tr>
	<tr>
		<td class="hcol">Tree</td>
		<td>0.95</td>
  		<td>1.11</td>
  		<td>1.45</td>
  		<td>2.10</td>
  		<td>3.65</td>
  	</tr>
	<tr>
		<td class="hcol">Force directed</td>
		<td>1.20</td>
  		<td>1.83</td>
  		<td>3.37</td>
  		<td>7.03</td>
  		<td>16.76</td>
  	</tr>
  	<tr>
		<th rowspan="4" class="rtitle"><small>Safari<br />5.0</small></th>
		<td class="hcol">Circle</td>
		<td>0.95</td>
		<td>1.15</td>
		<td>1.41</td>
		<td>1.98</td>
		<td>3.24</td>
	</tr>
	<tr>
		<td class="hcol">Radial</td>
		<td>1.03</td>
		<td>1.16</td>
		<td>1.46</td>
		<td>2.04</td>
		<td>3.63</td>
	</tr>
	<tr>
		<td class="hcol">Tree</td>
		<td>1.03</td>
		<td>1.15</td>
		<td>1.51</td>
		<td>2.05</td>
		<td>3.57</td>
	</tr>
	<tr>
		<td class="hcol">Force directed</td>
		<td>1.27</td>
		<td>1.84</td>
		<td>3.32</td>
		<td>6.94</td>
		<td>16.07</td>
	</tr>
	<tr>
		<th rowspan="12">Windows<br /> XP SP3</th>
		<th rowspan="4" class="rtitle"><small>Firefox<br /> 3.6.3</small></th>
		<td class="hcol">Circle</td>
		<td>0.78</td>
		<td>1.11</td>
		<td>1.27</td>
		<td>1.62</td>
		<td>2.46</td>
	</tr>
	<tr>
		<td class="hcol">Radial</td>
		<td>0.98</td>
		<td>1.07</td>
		<td>1.24</td>
		<td>1.64</td>
		<td>2.54</td>
	</tr>
	<tr>
		<td class="hcol">Tree</td>
		<td>1.00</td>
		<td>1.07</td>
		<td>1.25</td>
		<td>1.71</td>
		<td>2.57</td>
	</tr>
	<tr>
		<td class="hcol">Force directed</td>
		<td>1.17</td>
		<td>1.80</td>
		<td>3.53</td>
		<td>7.48</td>
		<td>17.61</td>
	</tr>
  	<tr>
		<th rowspan="4" class="rtitle"><small>IE 8</small></th>
		<td class="hcol">Circle</td>
		<td>0.78</td>
		<td>0.84</td>
		<td>1.00</td>
		<td>1.50</td>
		<td>2.28</td>
	</tr>
	<tr>
		<td class="hcol">Radial</td>
		<td>0.77</td>
		<td>0.86</td>
		<td>1.05</td>
		<td>1.47</td>
		<td>2.31</td>
	</tr>
	<tr>
		<td class="hcol">Tree</td>
		<td>0.80</td>
		<td>0.86</td>
		<td>1.05</td>
		<td>1.47</td>
		<td>2.34</td>
	</tr>
	<tr>
		<td class="hcol">Force directed</td>
		<td>0.97</td>
		<td>1.52</td>
		<td>2.83</td>
		<td>6.45</td>
		<td>15.05</td>
	</tr>
  	<tr>
		<th rowspan="4" class="rtitle"><small>Safari<br/> 5.0</small></th>
		<td class="hcol">Circle</td>
		<td>0.76</td>
		<td>0.93</td>
		<td>1.17</td>
		<td>1.55</td>
		<td>2.30</td>
	</tr>
	<tr>
		<td class="hcol">Radial</td>
		<td>0.85</td>
		<td>0.92</td>
		<td>1.12</td>
		<td>1.48</td>
		<td>2.33</td>
	</tr>
	<tr>
		<td class="hcol">Tree</td>
		<td>0.84</td>
		<td>0.92</td>
		<td>1.17</td>
		<td>1.51</td>
		<td>2.38</td>
	</tr>
	<tr>
		<td class="hcol">Force directed</td>
		<td>1.08</td>
		<td>1.61</td>
		<td>2.88</td>
		<td>6.23</td>
		<td>14.48</td>
	</tr>
</table>

<p><img src="/img/content/performance/loading_times_chart.png" /></p>
<p>Time to render a network with 2400 elements (800 nodes and 1600 edges), using different layout algorithms on supported operating systems and browsers.<p>

<hr/>
<p>Tests executed on an <i>Apple MacBook</i> computer with:
<ul>
	<li>GHz Intel Core 2 Duo processor</li>
	<li>4 GB RAM</li>
	<li>Adobe Flash Player version 10.1.</li>
</ul>