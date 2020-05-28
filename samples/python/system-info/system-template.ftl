[#ftl]

[#list data?keys as category]
	<h2>${category}</h2>
	[#assign categoryData = data[category]]
	[#if categoryData?is_enumerable]
		[#list categoryData as detail]
			<table>
				[#list detail?keys as detailKey]
					<tr>
						<td width="20%">${detailKey}</td>
						<td width="80%">
						[#if detail[detailKey]?is_sequence]
							[#list detail[detailKey] as detailDeep]
								<table>
									[#list detailDeep?keys as detailDeepKey]
									<tr>
										<td width="20%">${detailDeepKey}</td>
										<td width="80%">${detailDeep[detailDeepKey]!}</td>
									</tr>
									[/#list]
								</table>
								<br />
							[/#list]
						[#else]
							${detail[detailKey]}
						[/#if]
						</td>
					</tr>
				[/#list]
			</table>
			<br />
		[/#list]
	[#else]
		<table>
		[#list categoryData?keys as description]
			<tr>
				<td width="10%">${description}</td>
				<td width="90%">
				[#if categoryData[description]?is_hash]

					<table>
						[#list categoryData[description]?keys as descriptionDeep]
							<tr>
								<td>${descriptionDeep}</td>
								<td>${categoryData[description][descriptionDeep]}</td>
							</tr>
						[/#list]
					</table>
					<br />
				[#elseif categoryData[description]?is_sequence]
					[#list categoryData[description] as detail]
						<table>
							[#list detail?keys as detailKey]
							<tr>
								<td width="20%">${detailKey}</td>
								<td width="80%">${detail[detailKey]}</td>
							</tr>
							[/#list]
						</table>
						<br />
					[/#list]
				[#else]
					${categoryData[description]}
				[/#if]
				</td>
			</tr>
		[/#list]
		</table>
	[/#if]
[/#list]
