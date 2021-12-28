# Reddit Pager educational app

This app is a copy of the official android Paging With Network Sample.
https://github.com/android/architecture-components-samples/tree/main/PagingWithNetworkSample

It contains some minor improvements related to collecting of flows using
new lifeCycleScope API - repeatOnLifeCycle.

This sample demonstrates how to use the Paging library with a backend API (in this case Reddit API).

There are 3 variations of the demo, which you can select in the MainActivity class.

After selecting an option, it starts the RedditActivity which is the activity that shows the
list of posts in a given subreddit.

## Paging With Database And Network
This sample, implemented in the DbRedditPostRepository class, demonstrates how to set up
a Repository that will use the local database to page in data for the UI and also back-fill
the database from the network as the user reaches to the end of the data in the database.

It uses Room to create the PagingSource (dao). The Pager creates a stream of data from the
PagingSource to the UI, and more data is paged in as it is consumed.

This usually provides the best user experience as the cached content is always available on
the device and the user will still have a good experience even if the network is slow / unavailable.

## Paging Using Item Keys
This sample, implemented in the InMemoryByItemRepository class, demonstrates how to set up a
Repository that will directly page in from the network and will use the key from the previous
item to find the request parameters for the next page.

ItemKeyedSubredditPagingSource: The data source that uses the key in items (name in Reddit API)
to find the next page. It extends from the PagingSource class in the Paging Library.

## Paging Using Next Tokens From The Previous Query

This sample, implemented in the InMemoryByPageKeyRepository class, demonstrates how to utilize 
the before and after keys in the response to discover the next page. (This is the intended use 
of the Reddit API but this sample still provides ItemKeyedSubredditPagingSource to serve as an 
example if the backend does not provide before/after links)

PageKeyedSubredditPagingSource: The data source that uses the after and before fields in the 
API request response. It extends from the PagingSource class in the Paging Library.

### Libraries

- Android Support Library
- Android Architecture Components
- Retrofit for REST api communication
- Glide for image loading


